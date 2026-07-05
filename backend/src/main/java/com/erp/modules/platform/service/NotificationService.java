package com.erp.modules.platform.service;

import com.erp.modules.platform.dto.NotificationResponse;
import com.erp.modules.platform.entity.Notification;
import com.erp.modules.platform.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

  private final NotificationRepository notificationRepository;

  public NotificationService(NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  public NotificationResponse send(String title, String message, String type,
                                   String priority, String recipient, String module,
                                   String recordId) {
    Notification n = new Notification();
    n.setTitle(title);
    n.setMessage(message);
    n.setType(type);
    n.setPriority(priority);
    n.setRecipient(recipient);
    n.setModule(module);
    n.setRecordId(recordId);
    n.setStatus("UNREAD");
    Notification saved = notificationRepository.save(n);
    return toResponse(saved);
  }

  public void broadcast(String title, String message, String type, List<String> recipients,
                        String module, String recordId) {
    for (String recipient : recipients) {
      send(title, message, type, "NORMAL", recipient, module, recordId);
    }
  }

  @Transactional
  public void markAsRead(UUID id) {
    Notification n = notificationRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + id));
    n.setStatus("READ");
    n.setReadAt(LocalDateTime.now());
    notificationRepository.save(n);
  }

  @Transactional
  public void markAllAsRead(String recipient) {
    List<Notification> unread = notificationRepository.findByRecipientAndStatusOrderByCreatedAtDesc(recipient, "UNREAD");
    for (Notification n : unread) {
      n.setStatus("READ");
      n.setReadAt(LocalDateTime.now());
    }
    notificationRepository.saveAll(unread);
  }

  @Transactional
  public void dismiss(UUID id) {
    Notification n = notificationRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + id));
    n.setStatus("DISMISSED");
    notificationRepository.save(n);
  }

  public List<NotificationResponse> getByRecipient(String recipient) {
    return notificationRepository.findByRecipientOrderByCreatedAtDesc(recipient)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  public List<NotificationResponse> getUnread(String recipient) {
    return notificationRepository.findByRecipientAndStatusOrderByCreatedAtDesc(recipient, "UNREAD")
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  public long getUnreadCount(String recipient) {
    return notificationRepository.countByRecipientAndStatus(recipient, "UNREAD");
  }

  private NotificationResponse toResponse(Notification n) {
    NotificationResponse r = new NotificationResponse();
    r.setId(n.getId());
    r.setTitle(n.getTitle());
    r.setMessage(n.getMessage());
    r.setType(n.getType());
    r.setPriority(n.getPriority());
    r.setRecipient(n.getRecipient());
    r.setModule(n.getModule());
    r.setRecordId(n.getRecordId());
    r.setStatus(n.getStatus());
    r.setReadAt(n.getReadAt());
    r.setCreatedAt(n.getCreatedAt());
    return r;
  }
}
