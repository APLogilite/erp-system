package com.erp.modules.analytics.service;

import com.erp.modules.analytics.dto.KPIResponse;
import com.erp.modules.analytics.entity.KPIDefinition;
import com.erp.modules.analytics.repository.KPIDefinitionRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class KPIEngine {

  private final KPIDefinitionRepository kpiRepository;
  private final JdbcTemplate jdbcTemplate;
  private final Map<UUID, KPICacheEntry> cache = new ConcurrentHashMap<>();

  public KPIEngine(KPIDefinitionRepository kpiRepository, JdbcTemplate jdbcTemplate) {
    this.kpiRepository = kpiRepository;
    this.jdbcTemplate = jdbcTemplate;
  }

  public KPIResponse calculate(UUID kpiId) {
    KPICacheEntry cached = cache.get(kpiId);
    if (cached != null && !cached.isExpired()) {
      return cached.response;
    }

    KPIDefinition kpi = kpiRepository.findById(kpiId)
        .orElseThrow(() -> new IllegalArgumentException("KPI not found: " + kpiId));
    return calculate(kpi);
  }

  public KPIResponse calculate(KPIDefinition kpi) {
    KPIResponse response = new KPIResponse();
    response.setId(kpi.getId());
    response.setKpiCode(kpi.getKpiCode());
    response.setName(kpi.getName());
    response.setDescription(kpi.getDescription());
    response.setCategory(kpi.getCategory());
    response.setCalculationType(kpi.getCalculationType());
    response.setQueryExpression(kpi.getQueryExpression());
    response.setComparisonPeriod(kpi.getComparisonPeriod());
    response.setUnit(kpi.getUnit());
    response.setIsPercentage(kpi.getIsPercentage());
    response.setTargetValue(kpi.getTargetValue());
    response.setThresholdWarning(kpi.getThresholdWarning());
    response.setThresholdCritical(kpi.getThresholdCritical());
    response.setRefreshInterval(kpi.getRefreshInterval());
    response.setIsActive(kpi.getIsActive());
    response.setCreatedAt(kpi.getCreatedAt());
    response.setUpdatedAt(kpi.getUpdatedAt());

    try {
      Double currentValue = executeQuery(kpi.getQueryExpression());
      response.setCurrentValue(currentValue);

      if (kpi.getComparisonPeriod() != null) {
        String periodQuery = applyPeriodShift(kpi.getQueryExpression(), kpi.getComparisonPeriod());
        Double previousValue = executeQuery(periodQuery);
        response.setPreviousValue(previousValue);
        if (previousValue != null && previousValue != 0) {
          response.setChangePercent(((currentValue - previousValue) / previousValue) * 100);
        }
        response.setTrend(currentValue >= (previousValue != null ? previousValue : 0) ? "UP" : "DOWN");
      }
    } catch (Exception e) {
      response.setCurrentValue(0.0);
      response.setTrend("NONE");
    }

    cache.put(kpi.getId(), new KPICacheEntry(response, kpi.getRefreshInterval()));
    return response;
  }

  public List<KPIResponse> calculateAll() {
    List<KPIResponse> results = new ArrayList<>();
    for (KPIDefinition kpi : kpiRepository.findByIsActiveTrue()) {
      results.add(calculate(kpi));
    }
    return results;
  }

  public List<KPIResponse> calculateByCategory(String category) {
    List<KPIResponse> results = new ArrayList<>();
    for (KPIDefinition kpi : kpiRepository.findByCategory(category)) {
      results.add(calculate(kpi));
    }
    return results;
  }

  public void invalidateCache(UUID kpiId) {
    cache.remove(kpiId);
  }

  public void invalidateAll() {
    cache.clear();
  }

  private Double executeQuery(String query) {
    if (query == null || query.trim().isEmpty()) return 0.0;
    try {
      return Optional.ofNullable(jdbcTemplate.queryForObject(query, Double.class)).orElse(0.0);
    } catch (Exception e) {
      return 0.0;
    }
  }

  private String applyPeriodShift(String query, String period) {
    String today = LocalDate.now().toString();
    String periodStart;
    switch (period.toUpperCase()) {
      case "PREVIOUS_DAY": periodStart = LocalDate.now().minusDays(1).toString(); break;
      case "PREVIOUS_WEEK": periodStart = LocalDate.now().minusWeeks(1).toString(); break;
      case "PREVIOUS_MONTH": periodStart = LocalDate.now().minusMonths(1).toString(); break;
      case "PREVIOUS_QUARTER": periodStart = LocalDate.now().minusMonths(3).toString(); break;
      case "PREVIOUS_YEAR": periodStart = LocalDate.now().minusYears(1).toString(); break;
      default: return query;
    }
    return query.replace(today, periodStart);
  }

  private static class KPICacheEntry {
    final KPIResponse response;
    final long expiry;

    KPICacheEntry(KPIResponse response, int ttlSeconds) {
      this.response = response;
      this.expiry = System.currentTimeMillis() + (ttlSeconds * 1000L);
    }

    boolean isExpired() {
      return System.currentTimeMillis() > expiry;
    }
  }
}
