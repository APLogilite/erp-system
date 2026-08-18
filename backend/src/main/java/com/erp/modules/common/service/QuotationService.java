package com.erp.modules.common.service;

import com.erp.modules.common.dto.LineCalcRequest;
import com.erp.modules.common.dto.LineCalcResponse;
import com.erp.modules.common.dto.PriceResolveRequest;
import com.erp.modules.common.dto.QuotationTotalsResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
  private final DocumentSequenceService sequenceService;

  public QuotationService(NamedParameterJdbcTemplate jdbc, PriceResolutionService priceService,
                          DiscountResolutionService discountService,
                          DocumentSequenceService sequenceService) {
    this.jdbc = jdbc;
    this.priceService = priceService;
    this.discountService = discountService;
    this.sequenceService = sequenceService;
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

  /**
   * Converts an accepted quotation into a sales order.
   * Returns the new order id. Uses auto-numbering for the order number.
   */
  @Transactional
  public UUID convertToOrder(UUID quotationId, UUID tenantId) {
    Map<String, Object> quote = jdbc.queryForMap(
        "SELECT id, partner_id, currency, subtotal, discount_amount, tax_amount, grand_total, status "
          + "FROM tx_quotation WHERE id = :id",
        new MapSqlParameterSource("id", quotationId));
    String status = (String) quote.get("status");
    if (!"accepted".equals(status))
      throw new IllegalStateException("Quotation must be accepted before converting. Current status: " + status);

    // Generate order number
    String orderNumber = sequenceService.getNextNumber("sales_order", tenantId);

    // Create order
    UUID orderId = UUID.randomUUID();
    jdbc.update(
        "INSERT INTO tx_order (id, order_number, order_date, order_type, partner_id, currency, "
          + "subtotal, discount_amount, tax_amount, grand_total, status, tenant_id, created_at, updated_at) "
          + "VALUES (:id, :num, :date, 'sales', :pid, :cur, :s, :d, :t, :g, 'draft', :tid, now(), now())",
        new MapSqlParameterSource()
          .addValue("id", orderId).addValue("num", orderNumber)
          .addValue("date", LocalDate.now()).addValue("pid", quote.get("partner_id"))
          .addValue("cur", quote.get("currency")).addValue("s", quote.get("subtotal"))
          .addValue("d", quote.get("discount_amount")).addValue("t", quote.get("tax_amount"))
          .addValue("g", quote.get("grand_total")).addValue("tid", tenantId));

    // Copy lines
    List<Map<String, Object>> lines = jdbc.queryForList(
        "SELECT line_number, product_id, description, quantity, uom_id, unit_price, "
          + "discount_percent, discount_amount, line_total, tax_rate "
          + "FROM tx_quotation_line WHERE quotation_id = :qid AND is_active = true ORDER BY line_number",
        new MapSqlParameterSource("qid", quotationId));
    for (Map<String, Object> line : lines) {
      jdbc.update(
          "INSERT INTO tx_order_line (id, order_id, line_number, product_id, description, quantity, "
            + "uom_id, unit_price, line_total, tax_rate, tenant_id, created_at, updated_at) "
            + "VALUES (gen_random_uuid(), :oid, :ln, :pid, :desc, :qty, :uom, :up, :lt, :tr, :tid, now(), now())",
          new MapSqlParameterSource()
            .addValue("oid", orderId).addValue("ln", line.get("line_number"))
            .addValue("pid", line.get("product_id")).addValue("desc", line.get("description"))
            .addValue("qty", line.get("quantity")).addValue("uom", line.get("uom_id"))
            .addValue("up", line.get("unit_price")).addValue("lt", line.get("line_total"))
            .addValue("tr", line.get("tax_rate")).addValue("tid", tenantId));
    }

    // Mark quotation as converted
    jdbc.update(
        "UPDATE tx_quotation SET status = 'converted', converted_order_id = :oid, updated_at = now() WHERE id = :id",
        new MapSqlParameterSource().addValue("oid", orderId).addValue("id", quotationId));

    return orderId;
  }
}
