package com.erp.modules.crm.service;

import com.erp.common.base.BaseService;
import com.erp.modules.crm.entity.Opportunity;
import com.erp.modules.crm.repository.OpportunityRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OpportunityService extends BaseService<Opportunity> {

  private final OpportunityRepository opportunityRepository;

  public OpportunityService(OpportunityRepository opportunityRepository) {
    this.opportunityRepository = opportunityRepository;
  }

  @Override
  protected JpaRepository<Opportunity, UUID> getRepository() {
    return opportunityRepository;
  }

  @Override
  protected void beforeCreate(Opportunity entity) {
    if (entity.getOpportunityNumber() == null) {
      entity.setOpportunityNumber("OPP-" + System.currentTimeMillis());
    }
    if (entity.getStage() == null) {
      entity.setStage("OPEN");
    }
    if (entity.getBusinessPartnerId() == null) {
      throw new IllegalArgumentException("Business partner is required");
    }
  }

  @Transactional
  public UUID advanceStage(UUID oppId, String newStage) {
    Opportunity opp = findByIdOrThrow(oppId);
    String current = opp.getStage();
    if ("WON".equals(current) || "LOST".equals(current)) {
      throw new IllegalArgumentException("Cannot change stage of WON or LOST opportunity");
    }
    opp.setStage(newStage);
    return opportunityRepository.save(opp).getId();
  }

  @Transactional
  public UUID win(UUID oppId) {
    return advanceStage(oppId, "WON");
  }

  @Transactional
  public UUID lose(UUID oppId) {
    return advanceStage(oppId, "LOST");
  }
}
