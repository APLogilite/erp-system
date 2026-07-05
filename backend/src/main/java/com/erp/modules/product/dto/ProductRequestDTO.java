package com.erp.modules.product.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ProductRequestDTO {

    @NotNull
    private String code;

    @NotNull
    private String name;

    private String description;
    private String sku;
    private String barcode;
    private String uom;
    private String productType;
    private Boolean isStocked;
    private Boolean isSold;
    private Boolean isPurchased;
    private UUID categoryId;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public String getUom() { return uom; }
    public void setUom(String uom) { this.uom = uom; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public Boolean getIsStocked() { return isStocked; }
    public void setIsStocked(Boolean isStocked) { this.isStocked = isStocked; }

    public Boolean getIsSold() { return isSold; }
    public void setIsSold(Boolean isSold) { this.isSold = isSold; }

    public Boolean getIsPurchased() { return isPurchased; }
    public void setIsPurchased(Boolean isPurchased) { this.isPurchased = isPurchased; }

    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }
}
