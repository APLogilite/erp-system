package com.erp.modules.businesspartner.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class BusinessPartnerResponseDTO {

    private UUID id;
    private String code;
    private String name;
    private String partnerType;
    private String email;
    private String phone;
    private String mobile;
    private String website;
    private String taxId;
    private Boolean isCustomer;
    private Boolean isVendor;
    private Boolean isEmployee;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPartnerType() { return partnerType; }
    public void setPartnerType(String partnerType) { this.partnerType = partnerType; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public Boolean getIsCustomer() { return isCustomer; }
    public void setIsCustomer(Boolean isCustomer) { this.isCustomer = isCustomer; }

    public Boolean getIsVendor() { return isVendor; }
    public void setIsVendor(Boolean isVendor) { this.isVendor = isVendor; }

    public Boolean getIsEmployee() { return isEmployee; }
    public void setIsEmployee(Boolean isEmployee) { this.isEmployee = isEmployee; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
