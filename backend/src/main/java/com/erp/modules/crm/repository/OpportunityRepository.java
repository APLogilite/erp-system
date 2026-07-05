package com.erp.modules.crm.repository;

import com.erp.modules.crm.entity.Opportunity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, UUID> {
  Optional<Opportunity> findByOpportunityNumber(String opportunityNumber);
  List<Opportunity> findByStage(String stage);
  List<Opportunity> findByBusinessPartnerId(UUID businessPartnerId);
}
