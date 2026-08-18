package com.erp.modules.salesflow.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Enforces the sales order status lifecycle and records every transition
 * in tx_order_status_log.
 *
 * Allowed transitions:
 *   draft     → confirmed, cancelled
 *   confirmed → delivered, cancelled
 *   delivered → invoiced, cancelled (cancel only if no active invoice exists)
 *   invoiced  → paid
 *   any       → cancelled (blocked when a non-cancelled invoice exists)
 */
@Service
public class OrderWorkflowService {

  private static final Logger log = LoggerFactory.getLogger(OrderWorkflowService.class);

  private static final String STATUS_DRAFT = "draft";
  private static final String STATUS_CONFIRMED = "confirmed";
  private static final String STATUS_DELIVERED = "delivered";
  private static final String STATUS_INVOICED = "invoiced";
  private static final String STATUS_PAID = "paid";
  private static final String STATUS_CANCELLED = "cancelled";

  private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = Map.of(
      STATUS_DRAFT, Set.of(STATUS_CONFIRMED, STATUS_CANCELLED),
      STATUS_CONFIRMED, Set.of(STATUS_DELIVERED, STATUS_CANCELLED),
      STATUS_DELIVERED, Set.of(STATUS_INVOICED, STATUS_CANCELLED),
      STATUS_INVOICED, Set.of(STATUS_PAID, STATUS_CANCELLED),
      STATUS_PAID, Set.of(STATUS_CANCELLED),
      STATUS_CANCELLED, Set.of()
  );

  private final NamedParameterJdbcTemplate jdbc;

  public OrderWorkflowService(NamedParameterJdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  /**
   * Returns the list of valid target statuses for the given order.
   */
  public List<String> getAllowedTransitions(UUID orderId, UUID tenantId) {
    String current = queryCurrentStatus(orderId, tenantId);
    if (current == null) {
      return Collections.emptyList();
    }
    Set<String> targets = ALLOWED_TRANSITIONS.getOrDefault(current.toLowerCase(), Set.of());
    // Cancel is allowed unless a non-cancelled invoice exists
    if (targets.contains(STATUS_CANCELLED) && hasActiveInvoice(orderId, tenantId)) {
      return targets.stream().filter(s -> !STATUS_CANCELLED.equals(s)).toList();
    }
    return List.copyOf(targets);
  }

  /**
   * Attempts to transition the order to the target status.
   * Returns the new status, or throws IllegalStateException/IllegalArgumentException on failure.
   */
  @Transactional
  public String transition(UUID orderId, String targetStatus, UUID userId, UUID tenantId) {
    if (targetStatus == null || targetStatus.isBlank()) {
      throw new IllegalArgumentException("Target status is required");
    }
    String target = targetStatus.toLowerCase();

    String current = queryCurrentStatus(orderId, tenantId);
    if (current == null) {
      throw new IllegalArgumentException("Order not found: " + orderId);
    }
    String currentLower = current.toLowerCase();

    if (currentLower.equals(target)) {
      return current; // no-op
    }

    Set<String> allowed = ALLOWED_TRANSITIONS.getOrDefault(currentLower, Set.of());
    if (!allowed.contains(target)) {
      throw new IllegalStateException(
          "Cannot transition from " + currentLower + " to " + target);
    }

    if (STATUS_CANCELLED.equals(target) && hasActiveInvoice(orderId, tenantId)) {
      throw new IllegalStateException(
          "Cannot cancel order with active invoices");
    }

    // Update order status
    jdbc.update(
        "UPDATE tx_order SET status = :status, updated_at = now() WHERE id = :id AND tenant_id = :tid",
        new MapSqlParameterSource()
          .addValue("status", target)
          .addValue("id", orderId)
          .addValue("tid", tenantId));

    // Audit log
    jdbc.update(
        "INSERT INTO tx_order_status_log (id, order_id, from_status, to_status, transitioned_by, transitioned_at, tenant_id, created_at, updated_at, is_active) "
          + "VALUES (gen_random_uuid(), :oid, :from, :to, :user, now(), :tid, now(), now(), true)",
        new MapSqlParameterSource()
          .addValue("oid", orderId)
          .addValue("from", currentLower)
          .addValue("to", target)
          .addValue("user", userId)
          .addValue("tid", tenantId));

    log.info("Order {} transitioned from {} to {} by user {}", orderId, currentLower, target, userId);
    return target;
  }

  private String queryCurrentStatus(UUID orderId, UUID tenantId) {
    List<String> rows = jdbc.queryForList(
        "SELECT status FROM tx_order WHERE id = :id AND tenant_id = :tid AND is_active = true",
        new MapSqlParameterSource().addValue("id", orderId).addValue("tid", tenantId),
        String.class);
    return rows.isEmpty() ? null : rows.get(0);
  }

  private boolean hasActiveInvoice(UUID orderId, UUID tenantId) {
    Integer count = jdbc.queryForObject(
        "SELECT COUNT(*) FROM tx_invoice WHERE order_id = :oid AND tenant_id = :tid AND is_active = true AND status <> :cancelled",
        new MapSqlParameterSource()
          .addValue("oid", orderId)
          .addValue("tid", tenantId)
          .addValue("cancelled", STATUS_CANCELLED),
        Integer.class);
    return count != null && count > 0;
  }
}
