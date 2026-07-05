package com.erp.modules.platform.repository;

import com.erp.modules.platform.entity.Notification;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
  List<Notification> findByRecipientOrderByCreatedAtDesc(String recipient);
  List<Notification> findByRecipientAndStatusOrderByCreatedAtDesc(String recipient, String status);
  long countByRecipientAndStatus(String recipient, String status);
}
