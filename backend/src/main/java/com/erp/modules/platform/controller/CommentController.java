package com.erp.modules.platform.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.platform.dto.CommentResponse;
import com.erp.modules.platform.service.CommentService;
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
@RequestMapping(ApiVersionConfig.API_V1 + "/comments")
public class CommentController {

  private final CommentService commentService;

  public CommentController(CommentService commentService) {
    this.commentService = commentService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<CommentResponse>> addComment(
      @RequestParam String module,
      @RequestParam String recordId,
      @RequestParam String author,
      @RequestParam String body,
      @RequestParam(required = false) String parentId) {
    return ResponseEntity.ok(ApiResponse.success(
        commentService.addComment(module, recordId, author, body, parentId),
        "Comment added"));
  }

  @GetMapping("/by-relation")
  public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(
      @RequestParam String module, @RequestParam String recordId) {
    return ResponseEntity.ok(ApiResponse.success(
        commentService.getComments(module, recordId), "Comments retrieved"));
  }

  @GetMapping("/{parentId}/replies")
  public ResponseEntity<ApiResponse<List<CommentResponse>>> getReplies(@PathVariable String parentId) {
    return ResponseEntity.ok(ApiResponse.success(commentService.getReplies(parentId), "Replies retrieved"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable UUID id) {
    commentService.deleteComment(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Comment deleted"));
  }
}
