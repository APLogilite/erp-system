package com.erp.modules.platform.dto;

import java.util.UUID;

public class EmailTemplateResponse {
  private UUID id;
  private String code;
  private String name;
  private String subject;
  private String bodyHtml;
  private String bodyText;
  private String variables;
  private String locale;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
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
