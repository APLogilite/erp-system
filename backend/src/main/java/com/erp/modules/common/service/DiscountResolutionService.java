package com.erp.modules.common.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Finds the best applicable discount rule for a customer/product/quantity context.
 * Percentage discounts: discount = unitPrice × quantity × value / 100
 * Fixed discounts: discount = value (once per line)
 * Best = highest discountAmount wins.
 */
@Service
public class DiscountResolutionService {

  private static final Logger log = LoggerFactory.getLogger(DiscountResolutionService.class);

  private final NamedParameterJdbcTemplate jdbc;

  public DiscountResolutionService(NamedParameterJdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  /**
   * Returns a discount result (percent, amount, ruleId) or null if no rule matches.
   */
  public DiscountResult resolve(UUID partnerId, UUID productId, BigDecimal quantity,
                                 BigDecimal unitPrice) {
    LocalDate today = LocalDate.now();

    List<Map<String, Object>> rules = jdbc.queryForList(
        "SELECT id, discount_type, discount_value FROM md_discount_rule "
          + "WHERE is_active = true "
          + "AND (partner_id = :pid OR partner_id IS NULL) "
          + "AND (product_id = :prd OR product_id IS NULL) "
          + "AND (min_quantity IS NULL OR min_quantity <= :qty) "
          + "AND (valid_from IS NULL OR valid_from <= :today) "
          + "AND (valid_to IS NULL OR valid_to >= :today)",
        new MapSqlParameterSource()
          .addValue("pid", partnerId)
          .addValue("prd", productId)
          .addValue("qty", quantity != null ? quantity : BigDecimal.ONE)
          .addValue("today", today));

    if (rules.isEmpty()) return null;

    BigDecimal bestAmount = BigDecimal.ZERO;
    Map<String, Object> bestRule = null;

    for (Map<String, Object> r : rules) {
      String type = (String) r.get("discount_type");
      BigDecimal value = (BigDecimal) r.get("discount_value");
      BigDecimal amount;

      if ("percentage".equals(type)) {
        // unitPrice × quantity × value / 100
        BigDecimal qty = quantity != null ? quantity : BigDecimal.ONE;
        amount = unitPrice.multiply(qty).multiply(value).divide(new BigDecimal("100"), 2,
            java.math.RoundingMode.HALF_UP);
      } else { // fixed_amount
        amount = value;
      }

      if (amount.compareTo(bestAmount) > 0) {
        bestAmount = amount;
        bestRule = r;
      }
    }

    if (bestRule == null) return null;

    BigDecimal percent = "percentage".equals(bestRule.get("discount_type"))
        ? (BigDecimal) bestRule.get("discount_value") : null;

    return new DiscountResult(percent, bestAmount, (UUID) bestRule.get("id"));
  }

  public record DiscountResult(BigDecimal discountPercent, BigDecimal discountAmount, UUID discountRuleId) {}
}
