package com.erp.modules.crm.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.crm.dto.LeadRequest;
import com.erp.modules.crm.dto.LeadResponse;
import com.erp.modules.crm.entity.Lead;
import com.erp.modules.crm.service.LeadService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/leads")
public class LeadController {

  private final LeadService leadService;

  public LeadController(LeadService leadService) {
    this.leadService = leadService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<UUID>> create(@RequestBody LeadRequest request) {
    Lead entity = new Lead();
    entity.setCompany(request.getCompany());
    entity.setContactName(request.getContactName());
    entity.setEmail(request.getEmail());
    entity.setPhone(request.getPhone());
    entity.setSource(request.getSource());
    entity.setOwnerId(request.getOwnerId());
    entity.setExpectedValue(request.getExpectedValue());
    Lead saved = leadService.create(entity);
    return ResponseEntity.ok(ApiResponse.success(saved.getId(), "Lead created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<LeadResponse>>> getAll() {
    List<LeadResponse> list = leadService.findAll().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Leads retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<LeadResponse>> getById(@PathVariable UUID id) {
    Lead entity = leadService.findByIdOrThrow(id);
    return ResponseEntity.ok(ApiResponse.success(toResponse(entity), "Lead retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<LeadResponse>> update(@PathVariable UUID id, @RequestBody LeadRequest request) {
    Lead existing = leadService.findByIdOrThrow(id);
    existing.setCompany(request.getCompany());
    existing.setContactName(request.getContactName());
    existing.setEmail(request.getEmail());
    existing.setPhone(request.getPhone());
    existing.setSource(request.getSource());
    existing.setOwnerId(request.getOwnerId());
    existing.setExpectedValue(request.getExpectedValue());
    Lead updated = leadService.update(existing);
    return ResponseEntity.ok(ApiResponse.success(toResponse(updated), "Lead updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    leadService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Lead deleted"));
  }

  @PostMapping("/{id}/qualify")
  public ResponseEntity<ApiResponse<Void>> qualify(@PathVariable UUID id) {
    leadService.qualify(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Lead qualified"));
  }

  @PostMapping("/{id}/convert")
  public ResponseEntity<ApiResponse<Map<String, UUID>>> convert(@PathVariable UUID id, @RequestBody Map<String, UUID> body) {
    UUID businessPartnerId = body.get("businessPartnerId");
    UUID oppId = leadService.convert(id, businessPartnerId);
    return ResponseEntity.ok(ApiResponse.success(Map.of("opportunityId", oppId), "Lead converted to opportunity"));
  }

  @PostMapping("/{id}/close")
  public ResponseEntity<ApiResponse<Void>> close(@PathVariable UUID id) {
    leadService.close(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Lead closed"));
  }

  private LeadResponse toResponse(Lead entity) {
    LeadResponse r = new LeadResponse();
    r.setId(entity.getId());
    r.setLeadNumber(entity.getLeadNumber());
    r.setCompany(entity.getCompany());
    r.setContactName(entity.getContactName());
    r.setEmail(entity.getEmail());
    r.setPhone(entity.getPhone());
    r.setSource(entity.getSource());
    r.setStatus(entity.getStatus());
    r.setOwnerId(entity.getOwnerId());
    r.setExpectedValue(entity.getExpectedValue());
    r.setIsActive(entity.getIsActive());
    r.setCreatedAt(entity.getCreatedAt());
    r.setUpdatedAt(entity.getUpdatedAt());
    return r;
  }
}
