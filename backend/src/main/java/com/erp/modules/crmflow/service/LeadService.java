package com.erp.modules.crmflow.service;

import com.erp.modules.common.service.DocumentSequenceService;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeadService {

  private static final Logger log = LoggerFactory.getLogger(LeadService.class);

  private static final String STATUS_NEW = "new";
  private static final String STATUS_CONTACTED = "contacted";
  private static final String STATUS_QUALIFIED = "qualified";
  private static final String STATUS_CONVERTED = "converted";
  private static final String STATUS_LOST = "lost";

  private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = Map.of(
      STATUS_NEW, Set.of(STATUS_CONTACTED, STATUS_LOST),
      STATUS_CONTACTED, Set.of(STATUS_QUALIFIED, STATUS_LOST),
      STATUS_QUALIFIED, Set.of(STATUS_LOST),
      STATUS_CONVERTED, Set.of(),
      STATUS_LOST, Set.of()
  );

  private final NamedParameterJdbcTemplate jdbc;
  private final DocumentSequenceService sequenceService;

  public LeadService(NamedParameterJdbcTemplate jdbc, DocumentSequenceService sequenceService) {
    this.jdbc = jdbc;
    this.sequenceService = sequenceService;
  }

  @Transactional
  public String transition(UUID leadId, String targetStatus, UUID tenantId) {
    if (targetStatus == null || targetStatus.isBlank()) {
      throw new IllegalArgumentException("Target status is required");
    }
    String target = targetStatus.toLowerCase();

    Map<String, Object> lead = queryLead(leadId, tenantId);
    if (lead == null) {
      throw new IllegalArgumentException("Lead not found: " + leadId);
    }

    String current = String.valueOf(lead.get("status")).toLowerCase();
    if (current.equals(target)) {
      return current;
    }

    Set<String> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
    if (!allowed.contains(target)) {
      throw new IllegalStateException("Cannot transition lead from " + current + " to " + target);
    }

    jdbc.update(
        "UPDATE tx_lead SET status = :status, updated_at = now() WHERE id = :id AND tenant_id = :tenantId",
        new MapSqlParameterSource()
            .addValue("status", target)
            .addValue("id", leadId)
            .addValue("tenantId", tenantId));

    return target;
  }

  @Transactional
  public Map<String, String> convertToOpportunity(UUID leadId, UUID tenantId) {
    Map<String, Object> lead = queryLead(leadId, tenantId);
    if (lead == null) {
      throw new IllegalArgumentException("Lead not found: " + leadId);
    }

    String status = String.valueOf(lead.get("status")).toLowerCase();
    if (!STATUS_QUALIFIED.equals(status)) {
      throw new IllegalStateException("Only qualified leads can be converted");
    }

    if (lead.get("converted_opportunity_id") != null) {
      throw new IllegalStateException("Lead already converted");
    }

    UUID partnerId = lead.get("converted_partner_id") != null
        ? UUID.fromString(lead.get("converted_partner_id").toString())
        : null;

    String companyName = String.valueOf(lead.get("company_name"));
    String contactName = String.valueOf(lead.get("contact_name"));
    String email = lead.get("email") != null ? lead.get("email").toString() : null;
    String phone = lead.get("phone") != null ? lead.get("phone").toString() : null;

    if (partnerId == null) {
      partnerId = UUID.randomUUID();
      String leadNumber = String.valueOf(lead.get("lead_number"));
      jdbc.update(
          "INSERT INTO md_business_partner (id, code, name, email, phone, is_customer, is_vendor, is_active, tenant_id, created_at, updated_at) "
              + "VALUES (:id, :code, :name, :email, :phone, true, false, true, :tenantId, now(), now())",
          new MapSqlParameterSource()
              .addValue("id", partnerId)
              .addValue("code", leadNumber)
              .addValue("name", companyName)
              .addValue("email", email)
              .addValue("phone", phone)
              .addValue("tenantId", tenantId));

      jdbc.update(
          "UPDATE tx_lead SET converted_partner_id = :partnerId WHERE id = :id AND tenant_id = :tenantId",
          new MapSqlParameterSource()
              .addValue("partnerId", partnerId)
              .addValue("id", leadId)
              .addValue("tenantId", tenantId));
    }

    String opportunityNumber = sequenceService.getNextNumber("opportunity", tenantId);
    UUID opportunityId = UUID.randomUUID();
    String opportunityName = companyName + " Opportunity";
    BigDecimal expectedRevenue = toBigDecimal(lead.get("expected_revenue"));
    Object salespersonId = lead.get("salesperson_id");
    Object territoryId = lead.get("territory_id");

    jdbc.update(
        "INSERT INTO tx_opportunity (id, opportunity_number, name, partner_id, lead_id, stage, probability, expected_revenue, salesperson_id, territory_id, notes, tenant_id, is_active, created_at, updated_at) "
            + "VALUES (:id, :opportunityNumber, :name, :partnerId, :leadId, 'qualification', 25, :expectedRevenue, :salespersonId, :territoryId, :notes, :tenantId, true, now(), now())",
        new MapSqlParameterSource()
            .addValue("id", opportunityId)
            .addValue("opportunityNumber", opportunityNumber)
            .addValue("name", opportunityName)
            .addValue("partnerId", partnerId)
            .addValue("leadId", leadId)
            .addValue("expectedRevenue", expectedRevenue != null ? expectedRevenue : BigDecimal.ZERO)
            .addValue("salespersonId", salespersonId)
            .addValue("territoryId", territoryId)
            .addValue("notes", lead.get("notes"))
            .addValue("tenantId", tenantId));

    jdbc.update(
        "UPDATE tx_lead SET status = 'converted', converted_opportunity_id = :opportunityId, updated_at = now() "
            + "WHERE id = :id AND tenant_id = :tenantId",
        new MapSqlParameterSource()
            .addValue("opportunityId", opportunityId)
            .addValue("id", leadId)
            .addValue("tenantId", tenantId));

    return Map.of(
        "partnerId", partnerId.toString(),
        "opportunityId", opportunityId.toString(),
        "opportunityNumber", opportunityNumber);
  }

  private Map<String, Object> queryLead(UUID leadId, UUID tenantId) {
    var rows = jdbc.queryForList(
        "SELECT * FROM tx_lead WHERE id = :id AND tenant_id = :tenantId AND is_active = true",
        new MapSqlParameterSource()
            .addValue("id", leadId)
            .addValue("tenantId", tenantId));
    return rows.isEmpty() ? null : rows.get(0);
  }

  private BigDecimal toBigDecimal(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof BigDecimal bd) {
      return bd;
    }
    if (value instanceof Number n) {
      return BigDecimal.valueOf(n.doubleValue());
    }
    try {
      return new BigDecimal(value.toString());
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
