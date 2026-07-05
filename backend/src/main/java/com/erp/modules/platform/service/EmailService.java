package com.erp.modules.platform.service;

import com.erp.modules.platform.entity.EmailTemplate;
import com.erp.modules.platform.repository.EmailTemplateRepository;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private static final Logger log = LoggerFactory.getLogger(EmailService.class);

  private final EmailTemplateRepository emailTemplateRepository;

  public EmailService(EmailTemplateRepository emailTemplateRepository) {
    this.emailTemplateRepository = emailTemplateRepository;
  }

  public EmailTemplate createTemplate(String code, String name, String subject,
                                     String bodyHtml, String bodyText,
                                     String variables, String locale) {
    if (emailTemplateRepository.findByCode(code).isPresent()) {
      throw new IllegalArgumentException("Template already exists: " + code);
    }
    EmailTemplate t = new EmailTemplate();
    t.setCode(code);
    t.setName(name);
    t.setSubject(subject);
    t.setBodyHtml(bodyHtml);
    t.setBodyText(bodyText);
    t.setVariables(variables);
    if (locale == null) locale = "en";
    t.setLocale(locale);
    return emailTemplateRepository.save(t);
  }

  public EmailTemplate updateTemplate(String code, String subject, String bodyHtml,
                                     String bodyText, String variables) {
    EmailTemplate t = emailTemplateRepository.findByCode(code)
        .orElseThrow(() -> new IllegalArgumentException("Template not found: " + code));
    if (subject != null) t.setSubject(subject);
    if (bodyHtml != null) t.setBodyHtml(bodyHtml);
    if (bodyText != null) t.setBodyText(bodyText);
    if (variables != null) t.setVariables(variables);
    return emailTemplateRepository.save(t);
  }

  public String renderTemplate(String templateCode, Map<String, String> variables) {
    EmailTemplate t = emailTemplateRepository.findByCode(templateCode)
        .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateCode));
    String subject = t.getSubject();
    String body = t.getBodyHtml() != null ? t.getBodyHtml() : t.getBodyText();
    if (variables != null) {
      for (Map.Entry<String, String> entry : variables.entrySet()) {
        String placeholder = "{{" + entry.getKey() + "}}";
        subject = subject.replace(placeholder, entry.getValue());
        body = body.replace(placeholder, entry.getValue());
      }
    }
    return "Subject: " + subject + "\nBody: " + body;
  }

  public void sendEmail(String to, String subject, String body, boolean isHtml) {
    log.info("Sending email to={} subject={} isHtml={} bodyLength={}", to, subject, isHtml, body.length());
  }

  public java.util.List<EmailTemplate> getAllTemplates() {
    return emailTemplateRepository.findAll();
  }

  public EmailTemplate getByCode(String code) {
    return emailTemplateRepository.findByCode(code)
        .orElseThrow(() -> new IllegalArgumentException("Template not found: " + code));
  }
}
