package com.erp.platform.identity.controller;

import com.erp.common.api.ApiResponse;
import com.erp.platform.identity.entity.AuditRecord;
import com.erp.platform.identity.event.AuditQueryService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("identityAuditController")
@RequestMapping("/api/v1/identity/audit")
public class AuditController {

  private final AuditQueryService auditQueryService;

  public AuditController(AuditQueryService auditQueryService) {
    this.auditQueryService = auditQueryService;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<AuditRecord>>> getAll() {
    return ResponseEntity.ok(ApiResponse.success(auditQueryService.getAll(), "Audit records"));
  }

  @GetMapping("/by-user")
  public ResponseEntity<ApiResponse<List<AuditRecord>>> byUser(@RequestParam UUID userId) {
    return ResponseEntity.ok(ApiResponse.success(auditQueryService.getByUser(userId), "Audit records"));
  }

  @GetMapping("/by-username")
  public ResponseEntity<ApiResponse<List<AuditRecord>>> byUsername(@RequestParam String username) {
    return ResponseEntity.ok(ApiResponse.success(auditQueryService.getByUsername(username), "Audit records"));
  }

  @GetMapping("/by-type")
  public ResponseEntity<ApiResponse<List<AuditRecord>>> byType(@RequestParam String eventType) {
    return ResponseEntity.ok(ApiResponse.success(auditQueryService.getByEventType(eventType), "Audit records"));
  }

  @GetMapping("/by-date")
  public ResponseEntity<ApiResponse<List<AuditRecord>>> byDate(
      @RequestParam String from, @RequestParam String to) {
    LocalDateTime fromDate = LocalDate.parse(from).atStartOfDay();
    LocalDateTime toDate = LocalDate.parse(to).atTime(LocalTime.MAX);
    return ResponseEntity.ok(ApiResponse.success(
        auditQueryService.getByDateRange(fromDate, toDate), "Audit records"));
  }
}
