package com.erp.modules.crmflow.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CrmDashboardResponse {

  private Map<String, PipelineStageSummary> pipeline;
  private Map<String, BigDecimal> conversionRates;
  private List<TeamPerformanceRow> teamPerformance;

  public CrmDashboardResponse() {
  }

  public Map<String, PipelineStageSummary> getPipeline() {
    return pipeline;
  }

  public void setPipeline(Map<String, PipelineStageSummary> pipeline) {
    this.pipeline = pipeline;
  }

  public Map<String, BigDecimal> getConversionRates() {
    return conversionRates;
  }

  public void setConversionRates(Map<String, BigDecimal> conversionRates) {
    this.conversionRates = conversionRates;
  }

  public List<TeamPerformanceRow> getTeamPerformance() {
    return teamPerformance;
  }

  public void setTeamPerformance(List<TeamPerformanceRow> teamPerformance) {
    this.teamPerformance = teamPerformance;
  }

  public static class PipelineStageSummary {
    private long count;
    private BigDecimal totalRevenue;

    public PipelineStageSummary() {
    }

    public PipelineStageSummary(long count, BigDecimal totalRevenue) {
      this.count = count;
      this.totalRevenue = totalRevenue;
    }

    public long getCount() {
      return count;
    }

    public void setCount(long count) {
      this.count = count;
    }

    public BigDecimal getTotalRevenue() {
      return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
      this.totalRevenue = totalRevenue;
    }
  }

  public static class TeamPerformanceRow {
    private String salespersonId;
    private String name;
    private long openOpportunities;
    private BigDecimal closedRevenue;
    private BigDecimal commissionEarned;

    public TeamPerformanceRow() {
    }

    public String getSalespersonId() {
      return salespersonId;
    }

    public void setSalespersonId(String salespersonId) {
      this.salespersonId = salespersonId;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public long getOpenOpportunities() {
      return openOpportunities;
    }

    public void setOpenOpportunities(long openOpportunities) {
      this.openOpportunities = openOpportunities;
    }

    public BigDecimal getClosedRevenue() {
      return closedRevenue;
    }

    public void setClosedRevenue(BigDecimal closedRevenue) {
      this.closedRevenue = closedRevenue;
    }

    public BigDecimal getCommissionEarned() {
      return commissionEarned;
    }

    public void setCommissionEarned(BigDecimal commissionEarned) {
      this.commissionEarned = commissionEarned;
    }
  }
}
