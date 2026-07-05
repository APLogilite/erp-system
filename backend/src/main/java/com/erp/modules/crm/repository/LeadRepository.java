package com.erp.modules.crm.repository;

import com.erp.modules.crm.entity.Lead;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadRepository extends JpaRepository<Lead, UUID> {
  Optional<Lead> findByLeadNumber(String leadNumber);
  List<Lead> findByStatus(String status);
  List<Lead> findByOwnerId(UUID ownerId);
}
