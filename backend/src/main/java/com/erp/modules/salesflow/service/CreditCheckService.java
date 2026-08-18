package com.erp.modules.salesflow.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Evaluates customer credit for a sales order.
 *
 * outstanding = Σ open order grand_totals (confirmed/delivered)
 *             + Σ unpaid invoice due_amounts (validated/partially_paid)
 * approved    = not on credit_hold AND (credit_limit IS NULL OR outstanding + orderAmount ≤ credit_limit)
 */
@Service
public class CreditCheckService {

  private static final Logger log = LoggerFactory.getLogger(CreditCheckService.class);

  private final NamedParameterJdbcTemplate jdbc;

  public CreditCheckService(NamedParameterJdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  /**
   * Checks credit for the given order. Returns the full result including
   * credit limit, outstanding exposure, available credit, and order amount.
   */
  public CreditResult checkCredit(UUID orderId, UUID tenantId) {
    Map<String, Object> order = jdbc.queryForMap(
        "SELECT partner_id, grand_total FROM tx_order WHERE id = :id AND tenant_id = :tid AND is_active = true",
        new MapSqlParameterSource().addValue("id", orderId).addValue("tid", tenantId));

    UUID partnerId = (UUID) order.get("partner_id");
    BigDecimal orderAmount = (BigDecimal) order.get("grand_total");
    if (orderAmount == null) orderAmount = BigDecimal.ZERO;

    PartnerCredit credit = queryPartnerCredit(partnerId);
    BigDecimal outstanding = calculateOutstanding(partnerId, tenantId, orderId);

    CreditResult result = new CreditResult();
    result.creditLimit = credit.creditLimit;
    result.outstanding = outstanding;
    result.orderAmount = orderAmount;

    if (credit.creditHold) {
      result.approved = false;
      result.reason = "Customer is on credit hold";
    } else if (credit.creditLimit != null
        && outstanding.add(orderAmount).compareTo(credit.creditLimit) > 0) {
      result.approved = false;
      result.reason = "Credit limit exceeded";
    } else {
      result.approved = true;
      result.reason = "Approved";
    }

    if (credit.creditLimit != null) {
      result.available = credit.creditLimit.subtract(outstanding);
    }

    log.debug("Credit check for order {}: approved={}, reason={}, limit={}, outstanding={}, order={}",
        orderId, result.approved, result.reason, credit.creditLimit, outstanding, orderAmount);
    return result;
  }

  private PartnerCredit queryPartnerCredit(UUID partnerId) {
    List<Map<String, Object>> rows = jdbc.queryForList(
        "SELECT credit_limit, credit_hold FROM md_business_partner WHERE id = :id AND is_active = true",
        new MapSqlParameterSource("id", partnerId));
    if (rows.isEmpty()) {
      return new PartnerCredit(null, false);
    }
    Map<String, Object> row = rows.get(0);
    BigDecimal limit = (BigDecimal) row.get("credit_limit");
    Boolean hold = (Boolean) row.get("credit_hold");
    return new PartnerCredit(limit, hold != null && hold);
  }

  private BigDecimal calculateOutstanding(UUID partnerId, UUID tenantId, UUID excludeOrderId) {
    BigDecimal orders = jdbc.queryForObject(
        "SELECT COALESCE(SUM(grand_total), 0) FROM tx_order "
          + "WHERE partner_id = :pid AND tenant_id = :tid AND is_active = true "
          + "AND status IN ('confirmed','delivered') AND id <> :exclude",
        new MapSqlParameterSource()
          .addValue("pid", partnerId)
          .addValue("tid", tenantId)
          .addValue("exclude", excludeOrderId),
        BigDecimal.class);

    BigDecimal invoices = jdbc.queryForObject(
        "SELECT COALESCE(SUM(due_amount), 0) FROM tx_invoice "
          + "WHERE partner_id = :pid AND tenant_id = :tid AND is_active = true "
          + "AND status IN ('validated','partially_paid')",
        new MapSqlParameterSource().addValue("pid", partnerId).addValue("tid", tenantId),
        BigDecimal.class);

    return (orders != null ? orders : BigDecimal.ZERO)
        .add(invoices != null ? invoices : BigDecimal.ZERO);
  }

  private record PartnerCredit(BigDecimal creditLimit, boolean creditHold) {}

  public static class CreditResult {
    public boolean approved;
    public String reason;
    public BigDecimal creditLimit;
    public BigDecimal outstanding;
    public BigDecimal available;
    public BigDecimal orderAmount;
  }
}
