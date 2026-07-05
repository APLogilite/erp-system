package com.erp.platform.identity.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "identity_user_preferences")
public class UserPreference extends BaseEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private UserAccount user;

  @Column(name = "language", length = 10)
  private String language = "en";

  @Column(name = "timezone", length = 50)
  private String timezone = "UTC";

  @Column(name = "date_format", length = 20)
  private String dateFormat = "YYYY-MM-DD";

  @Column(name = "time_format", length = 20)
  private String timeFormat = "HH:mm";

  @Column(name = "number_format", length = 20)
  private String numberFormat = "#,##0.00";

  @Column(name = "currency", length = 3)
  private String currency = "USD";

  @Column(name = "theme", length = 20)
  private String theme = "light";

  @Column(name = "notifications_enabled")
  private Boolean notificationsEnabled = true;

  @Column(name = "items_per_page")
  private Integer itemsPerPage = 25;

  public UserAccount getUser() { return user; }
  public void setUser(UserAccount user) { this.user = user; }
  public String getLanguage() { return language; }
  public void setLanguage(String language) { this.language = language; }
  public String getTimezone() { return timezone; }
  public void setTimezone(String timezone) { this.timezone = timezone; }
  public String getDateFormat() { return dateFormat; }
  public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }
  public String getTimeFormat() { return timeFormat; }
  public void setTimeFormat(String timeFormat) { this.timeFormat = timeFormat; }
  public String getNumberFormat() { return numberFormat; }
  public void setNumberFormat(String numberFormat) { this.numberFormat = numberFormat; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
  public String getTheme() { return theme; }
  public void setTheme(String theme) { this.theme = theme; }
  public Boolean getNotificationsEnabled() { return notificationsEnabled; }
  public void setNotificationsEnabled(Boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }
  public Integer getItemsPerPage() { return itemsPerPage; }
  public void setItemsPerPage(Integer itemsPerPage) { this.itemsPerPage = itemsPerPage; }
}
