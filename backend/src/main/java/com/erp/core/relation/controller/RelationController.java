package com.erp.core.relation.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.relation.dto.LookupResultDto;
import com.erp.core.relation.service.RelationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/runtime")
public class RelationController {

  private final RelationService relationService;

  public RelationController(RelationService relationService) {
    this.relationService = relationService;
  }

  @GetMapping("/{model}/lookup")
  public ResponseEntity<ApiResponse<List<LookupResultDto>>> lookup(
      @PathVariable String model,
      @RequestParam(required = false, defaultValue = "") String search,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "20") int size) {

    List<LookupResultDto> results = relationService.lookup(model, search, page, size);
    return ResponseEntity.ok(ApiResponse.success(results, "Lookup results retrieved."));
  }

  @GetMapping("/{model}/autocomplete")
  public ResponseEntity<ApiResponse<List<LookupResultDto>>> autocomplete(
      @PathVariable String model,
      @RequestParam(required = false, defaultValue = "") String search,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "20") int size) {

    List<LookupResultDto> results = relationService.autocomplete(model, search, page, size);
    return ResponseEntity.ok(ApiResponse.success(results, "Autocomplete results retrieved."));
  }
}
