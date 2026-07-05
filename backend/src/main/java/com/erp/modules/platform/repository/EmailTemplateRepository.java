package com.erp.modules.platform.repository;

import com.erp.modules.platform.entity.EmailTemplate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, UUID> {
  Optional<EmailTemplate> findByCode(String code);
}
