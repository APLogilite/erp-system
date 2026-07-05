package com.erp.modules.platform.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.platform.dto.NotificationResponse;
import com.erp.modules.platform.service.NotificationService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/notifications")
public class NotificationController {

  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @GetMapping("/{recipient}")
  public ResponseEntity<ApiResponse<List<NotificationResponse>>> getByRecipient(@PathVariable String recipient) {
    return ResponseEntity.ok(ApiResponse.success(notificationService.getByRecipient(recipient), "Notifications retrieved"));
  }

  @GetMapping("/{recipient}/unread")
  public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnread(@PathVariable String recipient) {
    return ResponseEntity.ok(ApiResponse.success(notificationService.getUnread(recipient), "Unread notifications"));
  }

  @GetMapping("/{recipient}/count")
  public ResponseEntity<ApiResponse<Long>> getUnreadCount(@PathVariable String recipient) {
    return ResponseEntity.ok(ApiResponse.success(notificationService.getUnreadCount(recipient), "Unread count"));
  }

  @PostMapping("/{id}/read")
  public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable UUID id) {
    notificationService.markAsRead(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Notification marked as read"));
  }

  @PostMapping("/{recipient}/read-all")
  public ResponseEntity<ApiResponse<Void>> markAllAsRead(@PathVariable String recipient) {
    notificationService.markAllAsRead(recipient);
    return ResponseEntity.ok(ApiResponse.successMessage("All notifications marked as read"));
  }

  @PostMapping("/{id}/dismiss")
  public ResponseEntity<ApiResponse<Void>> dismiss(@PathVariable UUID id) {
    notificationService.dismiss(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Notification dismissed"));
  }

  @PostMapping("/send")
  public ResponseEntity<ApiResponse<NotificationResponse>> send(
      @RequestParam String title,
      @RequestParam String message,
      @RequestParam String type,
      @RequestParam String recipient,
      @RequestParam(required = false) String module,
      @RequestParam(required = false) String recordId) {
    return ResponseEntity.ok(ApiResponse.success(
        notificationService.send(title, message, type, "NORMAL", recipient, module, recordId),
        "Notification sent"));
  }
}
