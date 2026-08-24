package com.erp.modules.salesflow.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Calculates order header totals from line items and persists them.
 *
 * line_total = (quantity × unit_price) − discount_amount
 * subtotal   = Σ line_total
 * tax_amount = Σ(line_total × tax_rate / 100)
 * grand_total = subtotal + tax_amount
 */
@Service
public class OrderCalculationService {

  private static final Logger log = LoggerFactory.getLogger(OrderCalculationService.class);

  private final NamedParameterJdbcTemplate jdbc;

  public OrderCalculationService(NamedParameterJdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  /**
   * Calculates totals for the order and persists them to tx_order and tx_order_line.
   * Returns the computed header totals.
   */
  @Transactional
  public OrderTotals calculateAndPersist(UUID orderId, UUID tenantId) {
    verifyOrderExists(orderId, tenantId);

    List<Map<String, Object>> lines = jdbc.queryForList(
        "SELECT id, quantity, unit_price, discount_amount, tax_rate FROM tx_order_line "
          + "WHERE order_id = :oid AND is_active = true ORDER BY line_number",
        new MapSqlParameterSource("oid", orderId));

    BigDecimal subtotal = BigDecimal.ZERO;
    BigDecimal totalDiscount = BigDecimal.ZERO;
    BigDecimal totalTax = BigDecimal.ZERO;

    for (Map<String, Object> line : lines) {
      UUID lineId = (UUID) line.get("id");
      BigDecimal qty = (BigDecimal) line.get("quantity");
      BigDecimal unitPrice = (BigDecimal) line.get("unit_price");
      BigDecimal discount = (BigDecimal) line.get("discount_amount");
      BigDecimal taxRate = (BigDecimal) line.get("tax_rate");

      if (qty == null) qty = BigDecimal.ZERO;
      if (unitPrice == null) unitPrice = BigDecimal.ZERO;
      if (discount == null) discount = BigDecimal.ZERO;

      BigDecimal lineTotal = qty.multiply(unitPrice).subtract(discount);
      lineTotal = lineTotal.setScale(2, RoundingMode.HALF_UP);

      BigDecimal lineTax = BigDecimal.ZERO;
      if (taxRate != null && taxRate.compareTo(BigDecimal.ZERO) > 0) {
        lineTax = lineTotal.multiply(taxRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
      }

      jdbc.update(
          "UPDATE tx_order_line SET line_total = :lt WHERE id = :id",
          new MapSqlParameterSource().addValue("lt", lineTotal).addValue("id", lineId));

      subtotal = subtotal.add(lineTotal);
      totalDiscount = totalDiscount.add(discount);
      totalTax = totalTax.add(lineTax);
    }

    BigDecimal grandTotal = subtotal.add(totalTax);

    jdbc.update(
        "UPDATE tx_order SET subtotal = :s, discount_amount = :d, tax_amount = :t, grand_total = :g, updated_at = now() "
          + "WHERE id = :id AND tenant_id = :tid",
        new MapSqlParameterSource()
          .addValue("s", subtotal)
          .addValue("d", totalDiscount)
          .addValue("t", totalTax)
          .addValue("g", grandTotal)
          .addValue("id", orderId)
          .addValue("tid", tenantId));

    log.debug("Calculated totals for order {}: subtotal={}, discount={}, tax={}, grand={}",
        orderId, subtotal, totalDiscount, totalTax, grandTotal);

    return new OrderTotals(subtotal, totalDiscount, totalTax, grandTotal);
  }

  private void verifyOrderExists(UUID orderId, UUID tenantId) {
    Integer count = jdbc.queryForObject(
        "SELECT COUNT(*) FROM tx_order WHERE id = :id AND tenant_id = :tid AND is_active = true",
        new MapSqlParameterSource().addValue("id", orderId).addValue("tid", tenantId),
        Integer.class);
    if (count == null || count == 0) {
      throw new IllegalArgumentException("Order not found: " + orderId);
    }
  }

  public record OrderTotals(BigDecimal subtotal, BigDecimal discountAmount,
                             BigDecimal taxAmount, BigDecimal grandTotal) {}
}
