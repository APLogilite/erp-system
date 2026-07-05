package com.erp.modules.manufacturing.dto;

public class WorkCenterRequest {
  private String code;
  private String name;
  private Double capacity;
  private Double costPerHour;
  private Double efficiency;
  private String calendar;

  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public Double getCapacity() { return capacity; }
  public void setCapacity(Double capacity) { this.capacity = capacity; }
  public Double getCostPerHour() { return costPerHour; }
  public void setCostPerHour(Double costPerHour) { this.costPerHour = costPerHour; }
  public Double getEfficiency() { return efficiency; }
  public void setEfficiency(Double efficiency) { this.efficiency = efficiency; }
  public String getCalendar() { return calendar; }
  public void setCalendar(String calendar) { this.calendar = calendar; }
}
