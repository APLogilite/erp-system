package com.erp.modules.crmflow.service;

import com.erp.modules.common.service.DocumentSequenceService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OpportunityService {

  private static final Logger log = LoggerFactory.getLogger(OpportunityService.class);

  private static final String STAGE_QUALIFICATION = "qualification";
  private static final String STAGE_PROPOSAL = "proposal";
  private static final String STAGE_NEGOTIATION = "negotiation";
  private static final String STAGE_CLOSED_WON = "closed_won";
  private static final String STAGE_CLOSED_LOST = "closed_lost";

  private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = Map.of(
      STAGE_QUALIFICATION, Set.of(STAGE_PROPOSAL, STAGE_CLOSED_LOST),
      STAGE_PROPOSAL, Set.of(STAGE_NEGOTIATION, STAGE_CLOSED_LOST),
      STAGE_NEGOTIATION, Set.of(STAGE_CLOSED_WON, STAGE_CLOSED_LOST),
      STAGE_CLOSED_WON, Set.of(),
      STAGE_CLOSED_LOST, Set.of()
  );

  private static final Set<String> QUOTATION_ELIGIBLE_STAGES = Set.of(STAGE_PROPOSAL, STAGE_NEGOTIATION);

  private final NamedParameterJdbcTemplate jdbc;
  private final DocumentSequenceService sequenceService;
  private final BigDecimal defaultCommissionPercent;

  public OpportunityService(NamedParameterJdbcTemplate jdbc,
                            DocumentSequenceService sequenceService,
                            @Value("${crm.default-commission-percent:5.0}") BigDecimal defaultCommissionPercent) {
    this.jdbc = jdbc;
    this.sequenceService = sequenceService;
    this.defaultCommissionPercent = defaultCommissionPercent;
  }

  @Transactional
  public String transition(UUID opportunityId, String targetStage, UUID tenantId) {
    if (targetStage == null || targetStage.isBlank()) {
      throw new IllegalArgumentException("Target stage is required");
    }
    String target = targetStage.toLowerCase();

    Map<String, Object> opportunity = queryOpportunity(opportunityId, tenantId);
    if (opportunity == null) {
      throw new IllegalArgumentException("Opportunity not found: " + opportunityId);
    }

    String current = String.valueOf(opportunity.get("stage")).toLowerCase();
    if (current.equals(target)) {
      return current;
    }

    Set<String> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
    if (!allowed.contains(target)) {
      throw new IllegalStateException("Cannot transition opportunity from " + current + " to " + target);
    }

    jdbc.update(
        "UPDATE tx_opportunity SET stage = :stage, updated_at = now() WHERE id = :id AND tenant_id = :tenantId",
        new MapSqlParameterSource()
            .addValue("stage", target)
            .addValue("id", opportunityId)
            .addValue("tenantId", tenantId));

    if (STAGE_CLOSED_WON.equals(target)) {
      createCommission(opportunity, tenantId);
    }

    return target;
  }

  @Transactional
  public Map<String, String> createQuotation(UUID opportunityId, UUID tenantId) {
    Map<String, Object> opportunity = queryOpportunity(opportunityId, tenantId);
    if (opportunity == null) {
      throw new IllegalArgumentException("Opportunity not found: " + opportunityId);
    }

    String stage = String.valueOf(opportunity.get("stage")).toLowerCase();
    if (!QUOTATION_ELIGIBLE_STAGES.contains(stage)) {
      throw new IllegalStateException("Quotation can only be created from proposal or negotiation stage");
    }

    if (opportunity.get("converted_quotation_id") != null) {
      throw new IllegalStateException("Opportunity already has a quotation");
    }

    UUID quotationId = UUID.randomUUID();
    String quoteNumber = sequenceService.getNextNumber("quotation", tenantId);
    UUID partnerId = UUID.fromString(opportunity.get("partner_id").toString());
    BigDecimal expectedRevenue = toBigDecimal(opportunity.get("expected_revenue"));

    jdbc.update(
        "INSERT INTO tx_quotation (id, quote_number, quote_date, valid_until, partner_id, currency, grand_total, status, notes, tenant_id, is_active, created_at, updated_at) "
            + "VALUES (:id, :quoteNumber, CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', :partnerId, 'USD', :grandTotal, 'draft', :notes, :tenantId, true, now(), now())",
        new MapSqlParameterSource()
            .addValue("id", quotationId)
            .addValue("quoteNumber", quoteNumber)
            .addValue("partnerId", partnerId)
            .addValue("grandTotal", expectedRevenue != null ? expectedRevenue : BigDecimal.ZERO)
            .addValue("notes", opportunity.get("notes"))
            .addValue("tenantId", tenantId));

    jdbc.update(
        "UPDATE tx_opportunity SET converted_quotation_id = :quotationId, updated_at = now() WHERE id = :id AND tenant_id = :tenantId",
        new MapSqlParameterSource()
            .addValue("quotationId", quotationId)
            .addValue("id", opportunityId)
            .addValue("tenantId", tenantId));

    return Map.of("quotationId", quotationId.toString(), "quoteNumber", quoteNumber);
  }

  private void createCommission(Map<String, Object> opportunity, UUID tenantId) {
    Object salespersonId = opportunity.get("salesperson_id");
    if (salespersonId == null) {
      log.warn("Cannot create commission for opportunity {}: no salesperson assigned", opportunity.get("id"));
      return;
    }

    UUID opportunityId = UUID.fromString(opportunity.get("id").toString());
    BigDecimal expectedRevenue = toBigDecimal(opportunity.get("expected_revenue"));
    if (expectedRevenue == null) {
      expectedRevenue = BigDecimal.ZERO;
    }

    BigDecimal commissionAmount = expectedRevenue
        .multiply(defaultCommissionPercent)
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

    jdbc.update(
        "INSERT INTO tx_commission (id, salesperson_id, opportunity_id, commission_percent, commission_amount, status, tenant_id, is_active, created_at, updated_at) "
            + "VALUES (:id, :salespersonId, :opportunityId, :commissionPercent, :commissionAmount, 'pending', :tenantId, true, now(), now())",
        new MapSqlParameterSource()
            .addValue("id", UUID.randomUUID())
            .addValue("salespersonId", salespersonId)
            .addValue("opportunityId", opportunityId)
            .addValue("commissionPercent", defaultCommissionPercent)
            .addValue("commissionAmount", commissionAmount)
            .addValue("tenantId", tenantId));
  }

  private Map<String, Object> queryOpportunity(UUID opportunityId, UUID tenantId) {
    var rows = jdbc.queryForList(
        "SELECT * FROM tx_opportunity WHERE id = :id AND tenant_id = :tenantId AND is_active = true",
        new MapSqlParameterSource()
            .addValue("id", opportunityId)
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
