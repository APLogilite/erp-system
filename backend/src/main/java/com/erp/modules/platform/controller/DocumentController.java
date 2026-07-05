package com.erp.modules.platform.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.platform.dto.DocumentResponse;
import com.erp.modules.platform.service.DocumentService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/documents")
public class DocumentController {

  private final DocumentService documentService;

  public DocumentController(DocumentService documentService) {
    this.documentService = documentService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<DocumentResponse>> upload(
      @RequestParam String fileName,
      @RequestParam String mimeType,
      @RequestParam Long fileSize,
      @RequestParam String owner,
      @RequestParam(required = false) String module,
      @RequestParam(required = false) String recordId,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String folder,
      @RequestParam String filePath,
      @RequestParam(required = false) String contentType) {
    return ResponseEntity.ok(ApiResponse.success(
        documentService.upload(fileName, mimeType, fileSize, owner, module, recordId, category, folder, filePath, contentType),
        "Document uploaded"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<DocumentResponse>>> getAll() {
    return ResponseEntity.ok(ApiResponse.success(documentService.getAll(), "Documents retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<DocumentResponse>> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(ApiResponse.success(documentService.getById(id), "Document retrieved"));
  }

  @GetMapping("/by-relation")
  public ResponseEntity<ApiResponse<List<DocumentResponse>>> getByRelation(
      @RequestParam String module, @RequestParam String recordId) {
    return ResponseEntity.ok(ApiResponse.success(
        documentService.getByModuleAndRecord(module, recordId), "Documents retrieved"));
  }

  @PostMapping("/{id}/version")
  public ResponseEntity<ApiResponse<DocumentResponse>> newVersion(
      @PathVariable UUID id, @RequestParam String filePath, @RequestParam Long fileSize) {
    return ResponseEntity.ok(ApiResponse.success(
        documentService.newVersion(id, filePath, fileSize), "New version created"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    documentService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Document deleted"));
  }
}
