package com.erp.modules.service.service;

import com.erp.common.base.BaseService;
import com.erp.modules.service.entity.ServiceRequest;
import com.erp.modules.service.repository.ServiceRequestRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceRequestService extends BaseService<ServiceRequest> {

  private final ServiceRequestRepository serviceRequestRepository;

  public ServiceRequestService(ServiceRequestRepository serviceRequestRepository) {
    this.serviceRequestRepository = serviceRequestRepository;
  }

  @Override
  protected JpaRepository<ServiceRequest, UUID> getRepository() {
    return serviceRequestRepository;
  }

  @Override
  protected void beforeCreate(ServiceRequest entity) {
    if (entity.getTicketNumber() == null) {
      entity.setTicketNumber("SR-" + System.currentTimeMillis());
    }
    if (entity.getStatus() == null) {
      entity.setStatus("NEW");
    }
    if (entity.getPriority() == null) {
      entity.setPriority("MEDIUM");
    }
  }

  @Transactional
  public UUID assign(UUID ticketId, UUID engineerId) {
    ServiceRequest sr = findByIdOrThrow(ticketId);
    sr.setAssignedEngineerId(engineerId);
    if ("NEW".equals(sr.getStatus())) {
      sr.setStatus("ASSIGNED");
    }
    return serviceRequestRepository.save(sr).getId();
  }

  @Transactional
  public UUID start(UUID ticketId) {
    ServiceRequest sr = findByIdOrThrow(ticketId);
    if (!"ASSIGNED".equals(sr.getStatus())) {
      throw new IllegalArgumentException("Ticket must be ASSIGNED to start");
    }
    sr.setStatus("IN_PROGRESS");
    return serviceRequestRepository.save(sr).getId();
  }

  @Transactional
  public UUID resolve(UUID ticketId, String resolution) {
    ServiceRequest sr = findByIdOrThrow(ticketId);
    if (!"IN_PROGRESS".equals(sr.getStatus())) {
      throw new IllegalArgumentException("Only IN_PROGRESS tickets can be resolved");
    }
    sr.setStatus("RESOLVED");
    sr.setResolution(resolution);
    return serviceRequestRepository.save(sr).getId();
  }

  @Transactional
  public UUID close(UUID ticketId) {
    ServiceRequest sr = findByIdOrThrow(ticketId);
    if (!"RESOLVED".equals(sr.getStatus())) {
      throw new IllegalArgumentException("Only RESOLVED tickets can be closed");
    }
    sr.setStatus("CLOSED");
    return serviceRequestRepository.save(sr).getId();
  }
}
