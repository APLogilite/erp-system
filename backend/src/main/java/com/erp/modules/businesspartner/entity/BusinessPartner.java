package com.erp.modules.businesspartner.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "business_partners")
public class BusinessPartner extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    private String partnerType;

    private String email;

    private String phone;

    private String mobile;

    private String website;

    private String taxId;

    private Boolean isCustomer = false;

    private Boolean isVendor = false;

    private Boolean isEmployee = false;

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
}
