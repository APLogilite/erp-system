package com.erp.modules.purchase.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class PriceResolutionResponse {

  private UUID vendorId;
  private UUID productId;
  private UUID priceListId;
  private BigDecimal quantity;
  private BigDecimal unitPrice;
  private String currency;
  private String source; // PRICE_LIST or PRODUCT

  public PriceResolutionResponse() {}

  public PriceResolutionResponse(UUID vendorId, UUID productId, UUID priceListId,
                                 BigDecimal quantity, BigDecimal unitPrice,
                                 String currency, String source) {
    this.vendorId = vendorId;
    this.productId = productId;
    this.priceListId = priceListId;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.currency = currency;
    this.source = source;
  }

  public UUID getVendorId() { return vendorId; }
  public void setVendorId(UUID vendorId) { this.vendorId = vendorId; }

  public UUID getProductId() { return productId; }
  public void setProductId(UUID productId) { this.productId = productId; }

  public UUID getPriceListId() { return priceListId; }
  public void setPriceListId(UUID priceListId) { this.priceListId = priceListId; }

  public BigDecimal getQuantity() { return quantity; }
  public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

  public BigDecimal getUnitPrice() { return unitPrice; }
  public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }

  public String getSource() { return source; }
  public void setSource(String source) { this.source = source; }
}
