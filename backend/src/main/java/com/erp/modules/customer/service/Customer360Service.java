package com.erp.modules.customer.service;

import com.erp.modules.salesflow.service.CreditCheckService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
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
    return toCamelCase(rows.get(0));
  }

  public BigDecimal getOutstanding(UUID partnerId, UUID tenantId) {
    return creditCheckService.calculateOutstanding(partnerId, tenantId);
  }

  public List<Map<String, Object>> getRecentOrders(UUID partnerId, UUID tenantId) {
    List<Map<String, Object>> rows = jdbc.queryForList(
        "SELECT id, order_number, order_date, grand_total, status "
          + "FROM tx_order WHERE partner_id = :pid AND tenant_id = :tid AND is_active = true "
          + "ORDER BY order_date DESC, created_at DESC LIMIT :lim",
        new MapSqlParameterSource().addValue("pid", partnerId).addValue("tid", tenantId).addValue("lim", LIMIT));
    return mapRows(rows, Map.of("order_number", "orderNumber", "order_date", "orderDate", "grand_total", "grandTotal"));
  }

  public List<Map<String, Object>> getRecentInvoices(UUID partnerId, UUID tenantId) {
    List<Map<String, Object>> rows = jdbc.queryForList(
        "SELECT id, invoice_number, invoice_date, grand_total, due_amount, status "
          + "FROM tx_invoice WHERE partner_id = :pid AND tenant_id = :tid AND is_active = true "
          + "ORDER BY invoice_date DESC, created_at DESC LIMIT :lim",
        new MapSqlParameterSource().addValue("pid", partnerId).addValue("tid", tenantId).addValue("lim", LIMIT));
    return mapRows(rows, Map.of(
        "invoice_number", "invoiceNumber",
        "invoice_date", "invoiceDate",
        "grand_total", "grandTotal",
        "due_amount", "dueAmount"));
  }

  public List<Map<String, Object>> getRecentPayments(UUID partnerId, UUID tenantId) {
    List<Map<String, Object>> rows = jdbc.queryForList(
        "SELECT id, payment_number, payment_date, amount, status "
          + "FROM tx_payment WHERE partner_id = :pid AND tenant_id = :tid AND is_active = true "
          + "ORDER BY payment_date DESC, created_at DESC LIMIT :lim",
        new MapSqlParameterSource().addValue("pid", partnerId).addValue("tid", tenantId).addValue("lim", LIMIT));
    return mapRows(rows, Map.of("payment_number", "paymentNumber", "payment_date", "paymentDate"));
  }

  public List<Map<String, Object>> getOpenQuotations(UUID partnerId, UUID tenantId) {
    List<Map<String, Object>> rows = jdbc.queryForList(
        "SELECT id, quote_number, quote_date, grand_total, valid_until, status "
          + "FROM tx_quotation WHERE partner_id = :pid AND tenant_id = :tid AND is_active = true "
          + "AND status NOT IN ('converted','rejected','expired') "
          + "ORDER BY quote_date DESC, created_at DESC LIMIT :lim",
        new MapSqlParameterSource().addValue("pid", partnerId).addValue("tid", tenantId).addValue("lim", LIMIT));
    return mapRows(rows, Map.of(
        "quote_number", "quoteNumber",
        "quote_date", "quoteDate",
        "grand_total", "grandTotal",
        "valid_until", "validUntil"));
  }

  private List<Map<String, Object>> mapRows(List<Map<String, Object>> rows, Map<String, String> keyMapping) {
    if (rows == null || rows.isEmpty()) {
      return Collections.emptyList();
    }
    List<Map<String, Object>> result = new ArrayList<>(rows.size());
    for (Map<String, Object> row : rows) {
      Map<String, Object> mapped = new LinkedHashMap<>();
      for (Map.Entry<String, Object> entry : row.entrySet()) {
        String key = entry.getKey();
        if (key != null) {
          key = keyMapping.getOrDefault(key.toLowerCase(), key.toLowerCase());
        }
        mapped.put(key, entry.getValue());
      }
      result.add(mapped);
    }
    return result;
  }

  private Map<String, Object> toCamelCase(Map<String, Object> row) {
    if (row == null || row.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<String, Object> result = new LinkedHashMap<>();
    for (Map.Entry<String, Object> entry : row.entrySet()) {
      result.put(toCamelCaseKey(entry.getKey()), entry.getValue());
    }
    return result;
  }

  private String toCamelCaseKey(String key) {
    if (key == null || key.isEmpty()) {
      return key;
    }
    StringBuilder sb = new StringBuilder();
    boolean upperNext = false;
    for (char c : key.toCharArray()) {
      if (c == '_') {
        upperNext = true;
      } else if (upperNext) {
        sb.append(Character.toUpperCase(c));
        upperNext = false;
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }
}
