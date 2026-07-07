package com.erp.core.metadata.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.metadata.dto.CreateColumnRequest;
import com.erp.core.metadata.dto.CreateTableRequest;
import com.erp.core.metadata.dto.TableResponse;
import com.erp.core.metadata.service.TableDesignerService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/metadata/tables")
public class TableDesignerController {

  private final TableDesignerService service;

  public TableDesignerController(TableDesignerService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<TableResponse>>> listTables(
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(ApiResponse.success(service.listTables(search, page, size), "Tables retrieved."));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<TableResponse>> createTable(@RequestBody CreateTableRequest request) {
    return ResponseEntity.ok(ApiResponse.success(service.createTable(request), "Table created."));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<TableResponse>> getTable(@PathVariable UUID id) {
    return ResponseEntity.ok(ApiResponse.success(service.getTable(id), "Table retrieved."));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteTable(@PathVariable UUID id) {
    service.deleteTable(id);
    return ResponseEntity.ok(ApiResponse.success(null, "Table deleted."));
  }

  @PostMapping("/{id}/columns")
  public ResponseEntity<ApiResponse<TableResponse>> addColumn(
      @PathVariable UUID id, @RequestBody CreateColumnRequest request) {
    return ResponseEntity.ok(ApiResponse.success(service.addColumn(id, request), "Column added."));
  }

  @DeleteMapping("/{id}/columns/{colId}")
  public ResponseEntity<ApiResponse<Void>> deleteColumn(
      @PathVariable UUID id, @PathVariable UUID colId) {
    service.deleteColumn(id, colId);
    return ResponseEntity.ok(ApiResponse.success(null, "Column deleted."));
  }
}
