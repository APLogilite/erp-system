package com.erp.modules.platform.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.platform.dto.EmailTemplateResponse;
import com.erp.modules.platform.entity.EmailTemplate;
import com.erp.modules.platform.service.EmailService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/email")
public class EmailController {

  private final EmailService emailService;

  public EmailController(EmailService emailService) {
    this.emailService = emailService;
  }

  @GetMapping("/templates")
  public ResponseEntity<ApiResponse<List<EmailTemplateResponse>>> getTemplates() {
    List<EmailTemplateResponse> list = emailService.getAllTemplates().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Templates retrieved"));
  }

  @GetMapping("/templates/{code}")
  public ResponseEntity<ApiResponse<EmailTemplateResponse>> getTemplate(@PathVariable String code) {
    return ResponseEntity.ok(ApiResponse.success(toResponse(emailService.getByCode(code)), "Template retrieved"));
  }

  @PostMapping("/templates")
  public ResponseEntity<ApiResponse<EmailTemplateResponse>> createTemplate(@RequestBody EmailTemplate template) {
    EmailTemplate saved = emailService.createTemplate(
        template.getCode(), template.getName(), template.getSubject(),
        template.getBodyHtml(), template.getBodyText(),
        template.getVariables(), template.getLocale());
    return ResponseEntity.ok(ApiResponse.success(toResponse(saved), "Template created"));
  }

  @PostMapping("/templates/{code}/render")
  public ResponseEntity<ApiResponse<String>> renderTemplate(
      @PathVariable String code, @RequestBody(required = false) Map<String, String> variables) {
    return ResponseEntity.ok(ApiResponse.success(emailService.renderTemplate(code, variables), "Template rendered"));
  }

  @PostMapping("/send")
  public ResponseEntity<ApiResponse<Void>> send(
      @RequestParam String to,
      @RequestParam String subject,
      @RequestParam String body,
      @RequestParam(defaultValue = "false") boolean html) {
    emailService.sendEmail(to, subject, body, html);
    return ResponseEntity.ok(ApiResponse.successMessage("Email sent"));
  }

  private EmailTemplateResponse toResponse(EmailTemplate t) {
    EmailTemplateResponse r = new EmailTemplateResponse();
    r.setId(t.getId());
    r.setCode(t.getCode());
    r.setName(t.getName());
    r.setSubject(t.getSubject());
    r.setBodyHtml(t.getBodyHtml());
    r.setBodyText(t.getBodyText());
    r.setVariables(t.getVariables());
    r.setLocale(t.getLocale());
    return r;
  }
}
