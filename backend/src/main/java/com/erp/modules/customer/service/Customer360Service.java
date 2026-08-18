package com.erp.modules.customer.service;

import com.erp.modules.salesflow.service.CreditCheckService;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Builds a unified 360-degree view of a customer by aggregating profile,
 * credit exposure, orders, invoices, payments, and open quotations.
 */
@Service
public class Customer360Service {

  private static final Logger log = LoggerFactory.getLogger(Customer360Service.class);
  private static final int LIMIT = 5;

  private final NamedParameterJdbcTemplate jdbc;
  private final CreditCheckService creditCheckService;

  public Customer360Service(NamedParameterJdbcTemplate jdbc, CreditCheckService creditCheckService) {
    this.jdbc = jdbc;
    this.creditCheckService = creditCheckService;
  }

  public Map<String, Object> getPartnerProfile(UUID partnerId, UUID tenantId) {
    List<Map<String, Object>> rows = jdbc.queryForList(
        "SELECT id, code, name, email, phone, credit_limit, credit_hold, payment_term_id "
          + "FROM md_business_partner WHERE id = :id AND tenant_id = :tid AND is_active = true",
        new MapSqlParameterSource().addValue("id", partnerId).addValue("tid", tenantId));
    if (rows.isEmpty()) {
      return null;
    }
    return rows.get(0);
  }

  public BigDecimal getOutstanding(UUID partnerId, UUID tenantId) {
    return creditCheckService.calculateOutstanding(partnerId, tenantId);
  }

  public List<Map<String, Object>> getRecentOrders(UUID partnerId, UUID tenantId) {
    return jdbc.queryForList(
        "SELECT id, order_number as orderNumber, order_date as orderDate, grand_total as grandTotal, status "
          + "FROM tx_order WHERE partner_id = :pid AND tenant_id = :tid AND is_active = true "
          + "ORDER BY order_date DESC, created_at DESC LIMIT :lim",
        new MapSqlParameterSource().addValue("pid", partnerId).addValue("tid", tenantId).addValue("lim", LIMIT));
  }

  public List<Map<String, Object>> getRecentInvoices(UUID partnerId, UUID tenantId) {
    return jdbc.queryForList(
        "SELECT id, invoice_number as invoiceNumber, invoice_date as invoiceDate, grand_total as grandTotal, due_amount as dueAmount, status "
          + "FROM tx_invoice WHERE partner_id = :pid AND tenant_id = :tid AND is_active = true "
          + "ORDER BY invoice_date DESC, created_at DESC LIMIT :lim",
        new MapSqlParameterSource().addValue("pid", partnerId).addValue("tid", tenantId).addValue("lim", LIMIT));
  }

  public List<Map<String, Object>> getRecentPayments(UUID partnerId, UUID tenantId) {
    return jdbc.queryForList(
        "SELECT id, payment_number as paymentNumber, payment_date as paymentDate, amount, status "
          + "FROM tx_payment WHERE partner_id = :pid AND tenant_id = :tid AND is_active = true "
          + "ORDER BY payment_date DESC, created_at DESC LIMIT :lim",
        new MapSqlParameterSource().addValue("pid", partnerId).addValue("tid", tenantId).addValue("lim", LIMIT));
  }

  public List<Map<String, Object>> getOpenQuotations(UUID partnerId, UUID tenantId) {
    return jdbc.queryForList(
        "SELECT id, quote_number as quoteNumber, quote_date as quoteDate, grand_total as grandTotal, valid_until as validUntil, status "
          + "FROM tx_quotation WHERE partner_id = :pid AND tenant_id = :tid AND is_active = true "
          + "AND status NOT IN ('converted','rejected','expired') "
          + "ORDER BY quote_date DESC, created_at DESC LIMIT :lim",
        new MapSqlParameterSource().addValue("pid", partnerId).addValue("tid", tenantId).addValue("lim", LIMIT));
  }
}
