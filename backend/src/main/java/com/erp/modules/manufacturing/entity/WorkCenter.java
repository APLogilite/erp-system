package com.erp.modules.manufacturing.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "work_centers")
public class WorkCenter extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String code;

  @Column(nullable = false)
  private String name;

  @Column
  private Double capacity = 8.0;

  @Column(name = "cost_per_hour")
  private Double costPerHour = 0.0;

  @Column
  private Double efficiency = 100.0;

  @Column
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
