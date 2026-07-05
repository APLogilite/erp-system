package com.erp.modules.platform.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "email_templates")
public class EmailTemplate extends BaseEntity {

  @Column(name = "code", nullable = false, unique = true)
  private String code;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "subject", nullable = false)
  private String subject;

  @Column(name = "body_html", columnDefinition = "text")
  private String bodyHtml;

  @Column(name = "body_text", columnDefinition = "text")
  private String bodyText;

  @Column(name = "variables", columnDefinition = "text")
  private String variables;

  @Column(name = "locale", length = 10)
  private String locale;

  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getSubject() { return subject; }
  public void setSubject(String subject) { this.subject = subject; }
  public String getBodyHtml() { return bodyHtml; }
  public void setBodyHtml(String bodyHtml) { this.bodyHtml = bodyHtml; }
  public String getBodyText() { return bodyText; }
  public void setBodyText(String bodyText) { this.bodyText = bodyText; }
  public String getVariables() { return variables; }
  public void setVariables(String variables) { this.variables = variables; }
  public String getLocale() { return locale; }
  public void setLocale(String locale) { this.locale = locale; }
}
