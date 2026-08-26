package com.erp.modules.purchase.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pur_price_list")
public class PurchasePriceList extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String code;

  @Column(nullable = false)
  private String name;

  @Column(name = "vendor_id")
  private UUID vendorId;

  @Column(nullable = false)
  private String currency = "USD";

  @Column(name = "valid_from")
  private LocalDate validFrom;

  @Column(name = "valid_to")
  private LocalDate validTo;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  @OneToMany(mappedBy = "priceListId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<PurchasePriceListLine> lines = new ArrayList<>();

  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public UUID getVendorId() { return vendorId; }
  public void setVendorId(UUID vendorId) { this.vendorId = vendorId; }

  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }

  public LocalDate getValidFrom() { return validFrom; }
  public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

  public LocalDate getValidTo() { return validTo; }
  public void setValidTo(LocalDate validTo) { this.validTo = validTo; }

  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }

  public List<PurchasePriceListLine> getLines() { return lines; }
  public void setLines(List<PurchasePriceListLine> lines) { this.lines = lines; }
}
