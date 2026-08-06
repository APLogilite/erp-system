package com.erp.modules.common.service;

import com.erp.modules.common.dto.LineCalcRequest;
import com.erp.modules.common.dto.LineCalcResponse;
import com.erp.modules.common.dto.PriceResolveRequest;
import com.erp.modules.common.dto.QuotationTotalsResponse;
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
 * Calculates quotation line and header totals, using PriceResolutionService
 * and DiscountResolutionService for each line.
 */
@Service
public class QuotationService {

  private static final Logger log = LoggerFactory.getLogger(QuotationService.class);

  private final NamedParameterJdbcTemplate jdbc;
  private final PriceResolutionService priceService;
  private final DiscountResolutionService discountService;

  public QuotationService(NamedParameterJdbcTemplate jdbc, PriceResolutionService priceService,
                          DiscountResolutionService discountService) {
    this.jdbc = jdbc;
    this.priceService = priceService;
    this.discountService = discountService;
  }

  /**
   * Calculates and persists header totals for a quotation, resolving
   * price and discount for each line.
   */
  @Transactional
  public QuotationTotalsResponse calculateTotals(UUID quotationId) {
    // Load quotation header (partner, price_list, etc.)
    Map<String, Object> header = jdbc.queryForMap(
        "SELECT id, partner_id, price_list_id FROM tx_quotation WHERE id = :id",
        new MapSqlParameterSource("id", quotationId));

    UUID partnerId = (UUID) header.get("partner_id");

    // Load lines
    List<Map<String, Object>> lines = jdbc.queryForList(
        "SELECT id, product_id, quantity, unit_price, tax_rate FROM tx_quotation_line "
          + "WHERE quotation_id = :qid AND is_active = true ORDER BY line_number",
        new MapSqlParameterSource("qid", quotationId));

    BigDecimal subtotal = BigDecimal.ZERO;
    BigDecimal totalDiscount = BigDecimal.ZERO;
    BigDecimal totalTax = BigDecimal.ZERO;

    for (Map<String, Object> line : lines) {
      UUID lineId = (UUID) line.get("id");
      UUID productId = (UUID) line.get("product_id");
      BigDecimal qty = (BigDecimal) line.get("quantity");

      // Resolve price
      PriceResolveRequest pr = new PriceResolveRequest();
      pr.setProductId(productId);
      pr.setPartnerId(partnerId);
      pr.setQuantity(qty);
      var priceResp = priceService.resolve(pr);
      BigDecimal unitPrice = priceResp.getUnitPrice();

      // Resolve discount
      BigDecimal discountAmt = BigDecimal.ZERO;
      var disc = discountService.resolve(partnerId, productId, qty, unitPrice);
      if (disc != null) discountAmt = disc.discountAmount();

      // Line total = qty × unitPrice − discountAmount
      BigDecimal lineTotal = qty.multiply(unitPrice).subtract(discountAmt);

      // Tax
      BigDecimal taxRate = (BigDecimal) line.get("tax_rate");
      BigDecimal taxAmt = BigDecimal.ZERO;
      if (taxRate != null && taxRate.compareTo(BigDecimal.ZERO) > 0) {
        taxAmt = lineTotal.multiply(taxRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
      }

      // Persist line
      jdbc.update(
          "UPDATE tx_quotation_line SET unit_price = :up, discount_amount = :da, line_total = :lt, tax_rate = COALESCE(tax_rate, :tr) WHERE id = :id",
          new MapSqlParameterSource()
            .addValue("up", unitPrice).addValue("da", discountAmt)
            .addValue("lt", lineTotal).addValue("tr", taxRate)
            .addValue("id", lineId));

      subtotal = subtotal.add(lineTotal.add(discountAmt)); // before-discount total
      totalDiscount = totalDiscount.add(discountAmt);
      totalTax = totalTax.add(taxAmt);
    }

    BigDecimal grandTotal = subtotal.subtract(totalDiscount).add(totalTax);

    // Persist header
    jdbc.update(
        "UPDATE tx_quotation SET subtotal = :s, discount_amount = :d, tax_amount = :t, grand_total = :g WHERE id = :id",
        new MapSqlParameterSource().addValue("s", subtotal).addValue("d", totalDiscount)
          .addValue("t", totalTax).addValue("g", grandTotal).addValue("id", quotationId));

    QuotationTotalsResponse resp = new QuotationTotalsResponse();
    resp.setSubtotal(subtotal);
    resp.setDiscountAmount(totalDiscount);
    resp.setTaxAmount(totalTax);
    resp.setGrandTotal(grandTotal);
    return resp;
  }

  /**
   * Preview calculation for a single line (no persistence).
   */
  public LineCalcResponse calculateLine(LineCalcRequest req) {
    UUID productId = UUID.fromString(req.getProductId());
    UUID partnerId = req.getPartnerId() != null ? UUID.fromString(req.getPartnerId()) : null;
    BigDecimal qty = req.getQuantity() != null ? req.getQuantity() : BigDecimal.ONE;

    PriceResolveRequest pr = new PriceResolveRequest();
    pr.setProductId(productId);
    pr.setPartnerId(partnerId);
    pr.setQuantity(qty);
    var priceResp = priceService.resolve(pr);
    BigDecimal unitPrice = priceResp.getUnitPrice();

    BigDecimal discountPct = null;
    BigDecimal discountAmt = BigDecimal.ZERO;
    if (partnerId != null) {
      var disc = discountService.resolve(partnerId, productId, qty, unitPrice);
      if (disc != null) {
        discountPct = disc.discountPercent();
        discountAmt = disc.discountAmount();
      }
    }

    BigDecimal lineTotal = qty.multiply(unitPrice).subtract(discountAmt);
    BigDecimal taxAmt = BigDecimal.ZERO;
    if (req.getTaxRate() != null && req.getTaxRate().compareTo(BigDecimal.ZERO) > 0) {
      taxAmt = lineTotal.multiply(req.getTaxRate()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    LineCalcResponse resp = new LineCalcResponse();
    resp.setUnitPrice(unitPrice);
    resp.setDiscountPercent(discountPct);
    resp.setDiscountAmount(discountAmt);
    resp.setLineTotal(lineTotal);
    resp.setTaxAmount(taxAmt);
    return resp;
  }
}
