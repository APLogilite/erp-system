package com.erp.modules.crm.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.crm.dto.OpportunityRequest;
import com.erp.modules.crm.dto.OpportunityResponse;
import com.erp.modules.crm.entity.Opportunity;
import com.erp.modules.crm.service.OpportunityService;
import java.util.List;
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
@RequestMapping(ApiVersionConfig.API_V1 + "/opportunities")
public class OpportunityController {

  private final OpportunityService opportunityService;

  public OpportunityController(OpportunityService opportunityService) {
    this.opportunityService = opportunityService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<UUID>> create(@RequestBody OpportunityRequest request) {
    Opportunity entity = new Opportunity();
    entity.setBusinessPartnerId(request.getBusinessPartnerId());
    entity.setProbability(request.getProbability());
    entity.setExpectedRevenue(request.getExpectedRevenue());
    entity.setExpectedCloseDate(request.getExpectedCloseDate());
    entity.setSalespersonId(request.getSalespersonId());
    Opportunity saved = opportunityService.create(entity);
    return ResponseEntity.ok(ApiResponse.success(saved.getId(), "Opportunity created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<OpportunityResponse>>> getAll() {
    List<OpportunityResponse> list = opportunityService.findAll().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Opportunities retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<OpportunityResponse>> getById(@PathVariable UUID id) {
    Opportunity entity = opportunityService.findByIdOrThrow(id);
    return ResponseEntity.ok(ApiResponse.success(toResponse(entity), "Opportunity retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<OpportunityResponse>> update(@PathVariable UUID id, @RequestBody OpportunityRequest request) {
    Opportunity existing = opportunityService.findByIdOrThrow(id);
    existing.setBusinessPartnerId(request.getBusinessPartnerId());
    existing.setProbability(request.getProbability());
    existing.setExpectedRevenue(request.getExpectedRevenue());
    existing.setExpectedCloseDate(request.getExpectedCloseDate());
    existing.setSalespersonId(request.getSalespersonId());
    Opportunity updated = opportunityService.update(existing);
    return ResponseEntity.ok(ApiResponse.success(toResponse(updated), "Opportunity updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    opportunityService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Opportunity deleted"));
  }

  @PostMapping("/{id}/advance")
  public ResponseEntity<ApiResponse<Void>> advance(@PathVariable UUID id, @RequestBody String stage) {
    opportunityService.advanceStage(id, stage);
    return ResponseEntity.ok(ApiResponse.successMessage("Stage advanced"));
  }

  @PostMapping("/{id}/win")
  public ResponseEntity<ApiResponse<Void>> win(@PathVariable UUID id) {
    opportunityService.win(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Opportunity won"));
  }

  @PostMapping("/{id}/lose")
  public ResponseEntity<ApiResponse<Void>> lose(@PathVariable UUID id) {
    opportunityService.lose(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Opportunity lost"));
  }

  private OpportunityResponse toResponse(Opportunity entity) {
    OpportunityResponse r = new OpportunityResponse();
    r.setId(entity.getId());
    r.setOpportunityNumber(entity.getOpportunityNumber());
    r.setBusinessPartnerId(entity.getBusinessPartnerId());
    r.setStage(entity.getStage());
    r.setProbability(entity.getProbability());
    r.setExpectedRevenue(entity.getExpectedRevenue());
    r.setExpectedCloseDate(entity.getExpectedCloseDate());
    r.setSalespersonId(entity.getSalespersonId());
    r.setIsActive(entity.getIsActive());
    r.setCreatedAt(entity.getCreatedAt());
    r.setUpdatedAt(entity.getUpdatedAt());
    return r;
  }
}
