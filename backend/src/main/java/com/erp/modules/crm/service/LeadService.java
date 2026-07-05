package com.erp.modules.crm.service;

import com.erp.common.base.BaseService;
import com.erp.modules.crm.entity.Lead;
import com.erp.modules.crm.entity.Opportunity;
import com.erp.modules.crm.repository.LeadRepository;
import com.erp.modules.crm.repository.OpportunityRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeadService extends BaseService<Lead> {

  private final LeadRepository leadRepository;
  private final OpportunityRepository opportunityRepository;

  public LeadService(LeadRepository leadRepository, OpportunityRepository opportunityRepository) {
    this.leadRepository = leadRepository;
    this.opportunityRepository = opportunityRepository;
  }

  @Override
  protected JpaRepository<Lead, UUID> getRepository() {
    return leadRepository;
  }

  @Override
  protected void beforeCreate(Lead entity) {
    if (entity.getLeadNumber() == null) {
      entity.setLeadNumber("LD-" + System.currentTimeMillis());
    }
    if (entity.getStatus() == null) {
      entity.setStatus("NEW");
    }
  }

  @Override
  protected void beforeUpdate(Lead newEntity, Lead existingEntity) {
    String status = existingEntity.getStatus();
    if ("CLOSED".equals(status)) {
      throw new IllegalArgumentException("Cannot modify a CLOSED lead");
    }
    newEntity.setLeadNumber(existingEntity.getLeadNumber());
  }

  @Transactional
  public UUID qualify(UUID leadId) {
    Lead lead = findByIdOrThrow(leadId);
    if (!"NEW".equals(lead.getStatus())) {
      throw new IllegalArgumentException("Only NEW leads can be qualified");
    }
    lead.setStatus("QUALIFIED");
    return leadRepository.save(lead).getId();
  }

  @Transactional
  public UUID convert(UUID leadId, UUID businessPartnerId) {
    Lead lead = findByIdOrThrow(leadId);
    if (!"QUALIFIED".equals(lead.getStatus())) {
      throw new IllegalArgumentException("Only QUALIFIED leads can be converted");
    }

    Opportunity opp = new Opportunity();
    opp.setOpportunityNumber("OPP-" + System.currentTimeMillis());
    opp.setBusinessPartnerId(businessPartnerId);
    opp.setStage("OPEN");
    opp.setProbability(lead.getExpectedValue() > 0 ? 10.0 : 0.0);
    opp.setExpectedRevenue(lead.getExpectedValue());
    opp.setSalespersonId(lead.getOwnerId());
    opportunityRepository.save(opp);

    lead.setStatus("CONVERTED");
    leadRepository.save(lead);
    return opp.getId();
  }

  @Transactional
  public void close(UUID leadId) {
    Lead lead = findByIdOrThrow(leadId);
    if ("CLOSED".equals(lead.getStatus())) {
      throw new IllegalArgumentException("Lead is already CLOSED");
    }
    lead.setStatus("CLOSED");
    leadRepository.save(lead);
  }
}
