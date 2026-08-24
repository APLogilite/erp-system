package com.erp.modules.crmflow.service;

import com.erp.modules.crmflow.dto.CrmDashboardResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CrmDashboardService {

  private static final Logger log = LoggerFactory.getLogger(CrmDashboardService.class);

  private final NamedParameterJdbcTemplate jdbc;

  public CrmDashboardService(NamedParameterJdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public CrmDashboardResponse buildDashboard(UUID tenantId) {
    CrmDashboardResponse response = new CrmDashboardResponse();
    response.setPipeline(buildPipeline(tenantId));
    response.setConversionRates(buildConversionRates(tenantId));
    response.setTeamPerformance(buildTeamPerformance(tenantId));
    return response;
  }

  private Map<String, CrmDashboardResponse.PipelineStageSummary> buildPipeline(UUID tenantId) {
    Map<String, CrmDashboardResponse.PipelineStageSummary> result = new LinkedHashMap<>();
    result.put("qualification", new CrmDashboardResponse.PipelineStageSummary(0, BigDecimal.ZERO));
    result.put("proposal", new CrmDashboardResponse.PipelineStageSummary(0, BigDecimal.ZERO));
    result.put("negotiation", new CrmDashboardResponse.PipelineStageSummary(0, BigDecimal.ZERO));
    result.put("closedWon", new CrmDashboardResponse.PipelineStageSummary(0, BigDecimal.ZERO));
    result.put("closedLost", new CrmDashboardResponse.PipelineStageSummary(0, BigDecimal.ZERO));

    List<Map<String, Object>> rows = jdbc.queryForList(
        "SELECT stage, COUNT(*) AS cnt, COALESCE(SUM(expected_revenue), 0) AS revenue "
            + "FROM tx_opportunity WHERE tenant_id = :tenantId AND is_active = true "
            + "GROUP BY stage",
        new MapSqlParameterSource().addValue("tenantId", tenantId));

    for (Map<String, Object> row : rows) {
      String stage = String.valueOf(row.get("stage")).toLowerCase();
      long count = ((Number) row.get("cnt")).longValue();
      BigDecimal revenue = toBigDecimal(row.get("revenue"));
      String key = switch (stage) {
        case "qualification" -> "qualification";
        case "proposal" -> "proposal";
        case "negotiation" -> "negotiation";
        case "closed_won" -> "closedWon";
        case "closed_lost" -> "closedLost";
        default -> null;
      };
      if (key != null) {
        result.put(key, new CrmDashboardResponse.PipelineStageSummary(count, revenue));
      }
    }
    return result;
  }

  private Map<String, BigDecimal> buildConversionRates(UUID tenantId) {
    long totalLeads = count("SELECT COUNT(*) FROM tx_lead WHERE tenant_id = :tenantId AND is_active = true", tenantId);
    long convertedLeads = count("SELECT COUNT(*) FROM tx_lead WHERE tenant_id = :tenantId AND status = 'converted' AND is_active = true", tenantId);

    long totalOpportunities = count("SELECT COUNT(*) FROM tx_opportunity WHERE tenant_id = :tenantId AND is_active = true", tenantId);
    long wonOpportunities = count("SELECT COUNT(*) FROM tx_opportunity WHERE tenant_id = :tenantId AND stage = 'closed_won' AND is_active = true", tenantId);

    BigDecimal leadToOpportunity = rate(convertedLeads, totalLeads);
    BigDecimal opportunityToOrder = rate(wonOpportunities, totalOpportunities);

    Map<String, BigDecimal> rates = new LinkedHashMap<>();
    rates.put("leadToOpportunity", leadToOpportunity);
    rates.put("opportunityToOrder", opportunityToOrder);
    return rates;
  }

  private List<CrmDashboardResponse.TeamPerformanceRow> buildTeamPerformance(UUID tenantId) {
    List<Map<String, Object>> rows = jdbc.queryForList(
        "SELECT u.id AS salesperson_id, u.username AS name, "
            + "COALESCE(o.open_count, 0) AS open_opportunities, "
            + "COALESCE(o.closed_revenue, 0) AS closed_revenue, "
            + "COALESCE(c.commission_earned, 0) AS commission_earned "
            + "FROM identity_users u "
            + "LEFT JOIN ( "
            + "  SELECT salesperson_id, "
            + "    COUNT(*) FILTER (WHERE stage NOT IN ('closed_won','closed_lost')) AS open_count, "
            + "    COALESCE(SUM(expected_revenue) FILTER (WHERE stage = 'closed_won'), 0) AS closed_revenue "
            + "  FROM tx_opportunity "
            + "  WHERE tenant_id = :tenantId AND is_active = true "
            + "  GROUP BY salesperson_id "
            + ") o ON o.salesperson_id = u.id "
            + "LEFT JOIN ( "
            + "  SELECT salesperson_id, COALESCE(SUM(commission_amount), 0) AS commission_earned "
            + "  FROM tx_commission "
            + "  WHERE tenant_id = :tenantId AND status IN ('approved','paid') AND is_active = true "
            + "  GROUP BY salesperson_id "
            + ") c ON c.salesperson_id = u.id "
            + "WHERE u.is_active = true AND (o.salesperson_id IS NOT NULL OR c.salesperson_id IS NOT NULL) "
            + "ORDER BY closed_revenue DESC",
        new MapSqlParameterSource().addValue("tenantId", tenantId));

    List<CrmDashboardResponse.TeamPerformanceRow> result = new ArrayList<>();
    for (Map<String, Object> row : rows) {
      CrmDashboardResponse.TeamPerformanceRow r = new CrmDashboardResponse.TeamPerformanceRow();
      r.setSalespersonId(String.valueOf(row.get("salesperson_id")));
      r.setName(String.valueOf(row.get("name")));
      r.setOpenOpportunities(((Number) row.get("open_opportunities")).longValue());
      r.setClosedRevenue(toBigDecimal(row.get("closed_revenue")));
      r.setCommissionEarned(toBigDecimal(row.get("commission_earned")));
      result.add(r);
    }
    return result;
  }

  private long count(String sql, UUID tenantId) {
    Integer result = jdbc.queryForObject(sql, new MapSqlParameterSource().addValue("tenantId", tenantId), Integer.class);
    return result != null ? result.longValue() : 0L;
  }

  private BigDecimal rate(long numerator, long denominator) {
    if (denominator == 0) {
      return BigDecimal.ZERO;
    }
    return BigDecimal.valueOf(numerator)
        .divide(BigDecimal.valueOf(denominator), 4, RoundingMode.HALF_UP);
  }

  private BigDecimal toBigDecimal(Object value) {
    if (value == null) {
      return BigDecimal.ZERO;
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
      return BigDecimal.ZERO;
    }
  }
}
