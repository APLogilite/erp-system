package com.erp.modules.common.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Business logic for payment terms:
 * - Pre-fill order payment_term_id from the partner's default term.
 * - Auto-calculate invoice due_date from invoice_date + term days.
 */
@Service
public class PaymentTermService {

  private static final Logger log = LoggerFactory.getLogger(PaymentTermService.class);

  private final NamedParameterJdbcTemplate jdbc;

  public PaymentTermService(NamedParameterJdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  /**
   * If the order data does not contain a payment_term_id, look up the partner's
   * default payment_term_id and set it on the data map.
   */
  public void enrichOrderPaymentTerm(Map<String, Object> data) {
    if (data == null || data.get("payment_term_id") != null) {
      return;
    }
    Object partnerIdObj = data.get("partner_id");
    if (partnerIdObj == null) {
      return;
    }
    UUID partnerId = toUuid(partnerIdObj);
    if (partnerId == null) {
      return;
    }
    UUID termId = queryPartnerPaymentTerm(partnerId);
    if (termId != null) {
      data.put("payment_term_id", termId);
      log.debug("Pre-filled order payment_term_id {} from partner {}", termId, partnerId);
    }
  }

  /**
   * If the invoice data has a payment_term_id and invoice_date but no due_date,
   * calculate due_date = invoice_date + term.days.
   */
  public void enrichInvoiceDueDate(Map<String, Object> data) {
    if (data == null) {
      return;
    }
    Object termIdObj = data.get("payment_term_id");
    Object invoiceDateObj = data.get("invoice_date");
    if (termIdObj == null || invoiceDateObj == null) {
      return;
    }
    if (data.get("due_date") != null) {
      return; // respect explicit due_date
    }
    UUID termId = toUuid(termIdObj);
    LocalDate invoiceDate = toLocalDate(invoiceDateObj);
    if (termId == null || invoiceDate == null) {
      return;
    }
    Integer days = queryTermDays(termId);
    if (days != null) {
      LocalDate dueDate = invoiceDate.plus(days, ChronoUnit.DAYS);
      data.put("due_date", dueDate);
      log.debug("Calculated invoice due_date {} from term {} days {}", dueDate, termId, days);
    }
  }

  private UUID queryPartnerPaymentTerm(UUID partnerId) {
    try {
      return jdbc.queryForObject(
          "SELECT payment_term_id FROM md_business_partner WHERE id = :id AND is_active = true",
          new MapSqlParameterSource("id", partnerId),
          UUID.class);
    } catch (Exception e) {
      return null;
    }
  }

  private Integer queryTermDays(UUID termId) {
    try {
      return jdbc.queryForObject(
          "SELECT days FROM md_payment_term WHERE id = :id AND is_active = true",
          new MapSqlParameterSource("id", termId),
          Integer.class);
    } catch (Exception e) {
      return null;
    }
  }

  private UUID toUuid(Object value) {
    if (value instanceof UUID uuid) {
      return uuid;
    }
    if (value instanceof String s) {
      try {
        return UUID.fromString(s);
      } catch (IllegalArgumentException e) {
        return null;
      }
    }
    return null;
  }

  private LocalDate toLocalDate(Object value) {
    if (value instanceof LocalDate d) {
      return d;
    }
    if (value instanceof java.sql.Date sd) {
      return sd.toLocalDate();
    }
    if (value instanceof java.util.Date ud) {
      return new java.sql.Date(ud.getTime()).toLocalDate();
    }
    if (value instanceof String s) {
      try {
        return LocalDate.parse(s);
      } catch (Exception e) {
        return null;
      }
    }
    return null;
  }
}
