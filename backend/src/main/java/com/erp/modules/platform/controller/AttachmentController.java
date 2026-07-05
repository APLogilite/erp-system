package com.erp.modules.platform.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.platform.entity.Attachment;
import com.erp.modules.platform.service.AttachmentService;
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
@RequestMapping(ApiVersionConfig.API_V1 + "/attachments")
public class AttachmentController {

  private final AttachmentService attachmentService;

  public AttachmentController(AttachmentService attachmentService) {
    this.attachmentService = attachmentService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<Attachment>> attach(
      @RequestParam String fileName,
      @RequestParam String mimeType,
      @RequestParam Long fileSize,
      @RequestParam String module,
      @RequestParam String recordId,
      @RequestParam String filePath,
      @RequestParam(required = false) String description) {
    return ResponseEntity.ok(ApiResponse.success(
        attachmentService.attach(fileName, mimeType, fileSize, module, recordId, filePath, description),
        "Attachment created"));
  }

  @GetMapping("/by-relation")
  public ResponseEntity<ApiResponse<List<Attachment>>> getByRelation(
      @RequestParam String module, @RequestParam String recordId) {
    return ResponseEntity.ok(ApiResponse.success(
        attachmentService.getAttachments(module, recordId), "Attachments retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Attachment>> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(ApiResponse.success(attachmentService.getById(id), "Attachment retrieved"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> detach(@PathVariable UUID id) {
    attachmentService.detach(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Attachment deleted"));
  }

  @DeleteMapping("/by-relation")
  public ResponseEntity<ApiResponse<Void>> detachAll(
      @RequestParam String module, @RequestParam String recordId) {
    attachmentService.detachAll(module, recordId);
    return ResponseEntity.ok(ApiResponse.successMessage("All attachments deleted"));
  }
}
