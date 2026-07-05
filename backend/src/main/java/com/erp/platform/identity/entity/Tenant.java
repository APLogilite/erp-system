package com.erp.platform.identity.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "identity_tenants")
public class Tenant extends BaseEntity {

  @Column(name = "code", nullable = false, unique = true, length = 50)
  private String code;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "domain", length = 255)
  private String domain;

  @Column(name = "logo_url", length = 500)
  private String logoUrl;

  @Column(name = "default_language", length = 10)
  private String defaultLanguage;

  @Column(name = "default_timezone", length = 50)
  private String defaultTimezone;

  @Column(name = "default_currency", length = 3)
  private String defaultCurrency;

  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getDomain() { return domain; }
  public void setDomain(String domain) { this.domain = domain; }
  public String getLogoUrl() { return logoUrl; }
  public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
  public String getDefaultLanguage() { return defaultLanguage; }
  public void setDefaultLanguage(String defaultLanguage) { this.defaultLanguage = defaultLanguage; }
  public String getDefaultTimezone() { return defaultTimezone; }
  public void setDefaultTimezone(String defaultTimezone) { this.defaultTimezone = defaultTimezone; }
  public String getDefaultCurrency() { return defaultCurrency; }
  public void setDefaultCurrency(String defaultCurrency) { this.defaultCurrency = defaultCurrency; }
}
