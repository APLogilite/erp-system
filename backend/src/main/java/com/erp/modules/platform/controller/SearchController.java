package com.erp.modules.platform.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.platform.dto.SearchResultResponse;
import com.erp.modules.platform.service.GlobalSearchService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/search")
public class SearchController {

  private final GlobalSearchService globalSearchService;

  public SearchController(GlobalSearchService globalSearchService) {
    this.globalSearchService = globalSearchService;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<SearchResultResponse>>> search(
      @RequestParam String q,
      @RequestParam(required = false) String module,
      @RequestParam(defaultValue = "20") int limit) {
    return ResponseEntity.ok(ApiResponse.success(
        globalSearchService.search(q, module, limit), "Search results"));
  }

  @GetMapping("/autocomplete")
  public ResponseEntity<ApiResponse<List<String>>> autocomplete(
      @RequestParam String q,
      @RequestParam(defaultValue = "10") int limit) {
    return ResponseEntity.ok(ApiResponse.success(
        globalSearchService.autocomplete(q, limit), "Autocomplete results"));
  }
}
