package com.erp.modules.crmflow.hook;

import com.erp.core.runtime.hook.RecordLifecycleHook;
import com.erp.modules.common.service.DocumentSequenceService;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Applies CRM-specific defaults during dynamic CRUD:
 * - auto-numbers leads and opportunities
 * - auto-scores leads based on expected revenue and source
 */
@Component
public class CrmRecordLifecycleHook implements RecordLifecycleHook {

  private static final Logger log = LoggerFactory.getLogger(CrmRecordLifecycleHook.class);

  private static final String TABLE_LEAD = "tx_lead";
  private static final String TABLE_OPPORTUNITY = "tx_opportunity";

  private static final Set<String> SUPPORTED = Set.of(TABLE_LEAD, TABLE_OPPORTUNITY);

  private final DocumentSequenceService sequenceService;

  public CrmRecordLifecycleHook(DocumentSequenceService sequenceService) {
    this.sequenceService = sequenceService;
  }

  @Override
  public boolean supports(String tableName) {
    return SUPPORTED.contains(tableName);
  }

  @Override
  public void beforeCreate(String tableName, Map<String, Object> data, UUID tenantId, UUID userId) {
    if (TABLE_LEAD.equals(tableName)) {
      if (isBlank(data.get("lead_number"))) {
        data.put("lead_number", sequenceService.getNextNumber("lead", tenantId));
      }
      data.put("score", computeScore(data));
    } else if (TABLE_OPPORTUNITY.equals(tableName)) {
      if (isBlank(data.get("opportunity_number"))) {
        data.put("opportunity_number", sequenceService.getNextNumber("opportunity", tenantId));
      }
    }
  }

  @Override
  public void beforeUpdate(String tableName, UUID recordId, Map<String, Object> data, UUID tenantId, UUID userId) {
    if (TABLE_LEAD.equals(tableName)) {
      // Score is always server-computed; strip any client value and recompute
      data.remove("score");
      data.put("score", computeScore(data));
    }
  }

  private String computeScore(Map<String, Object> data) {
    BigDecimal revenue = toBigDecimal(data.get("expected_revenue"));
    String source = toString(data.get("source"));

    if (revenue != null && revenue.compareTo(new BigDecimal("50000")) > 0
        || "referral".equalsIgnoreCase(source)) {
      return "hot";
    }
    if (revenue != null && revenue.compareTo(new BigDecimal("10000")) > 0
        || "website".equalsIgnoreCase(source)
        || "trade_show".equalsIgnoreCase(source)) {
      return "warm";
    }
    return "cold";
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

  private String toString(Object value) {
    return value == null ? null : value.toString();
  }

  private boolean isBlank(Object value) {
    return value == null || value.toString().isBlank();
  }
}
