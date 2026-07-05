package com.erp.modules.manufacturing.service;

import com.erp.common.base.BaseService;
import com.erp.modules.manufacturing.dto.RoutingOperationRequest;
import com.erp.modules.manufacturing.dto.RoutingRequest;
import com.erp.modules.manufacturing.entity.Routing;
import com.erp.modules.manufacturing.entity.RoutingOperation;
import com.erp.modules.manufacturing.repository.RoutingOperationRepository;
import com.erp.modules.manufacturing.repository.RoutingRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoutingService extends BaseService<Routing> {

  private final RoutingRepository routingRepository;
  private final RoutingOperationRepository operationRepository;

  public RoutingService(RoutingRepository routingRepository,
                        RoutingOperationRepository operationRepository) {
    this.routingRepository = routingRepository;
    this.operationRepository = operationRepository;
  }

  @Override
  protected JpaRepository<Routing, UUID> getRepository() {
    return routingRepository;
  }

  @Override
  protected void beforeCreate(Routing entity) {
    if (entity.getCode() == null || entity.getCode().trim().isEmpty()) {
      throw new IllegalArgumentException("Routing code is required");
    }
    if (routingRepository.findByCode(entity.getCode()).isPresent()) {
      throw new IllegalArgumentException("Routing code must be unique");
    }
  }

  @Override
  protected void beforeUpdate(Routing newEntity, Routing existingEntity) {
    if (!newEntity.getCode().equals(existingEntity.getCode())
        && routingRepository.findByCode(newEntity.getCode()).isPresent()) {
      throw new IllegalArgumentException("Routing code must be unique");
    }
  }

  @Transactional
  public UUID createWithOperations(RoutingRequest request) {
    Routing routing = new Routing();
    routing.setCode(request.getCode());
    routing.setName(request.getName());
    routing.setDescription(request.getDescription());

    beforeCreate(routing);
    Routing saved = routingRepository.save(routing);

    if (request.getOperations() != null && !request.getOperations().isEmpty()) {
      processOperations(saved.getId(), request.getOperations());
    }

    return saved.getId();
  }

  public List<RoutingOperation> getOperations(UUID routingId) {
    return operationRepository.findByRoutingIdOrderBySequenceAsc(routingId);
  }

  private void processOperations(UUID routingId, List<RoutingOperationRequest> requests) {
    int sequence = 1;
    for (RoutingOperationRequest req : requests) {
      RoutingOperation op = new RoutingOperation();
      op.setRoutingId(routingId);
      op.setSequence(sequence++);
      op.setWorkCenterId(req.getWorkCenterId());
      op.setOperationName(req.getOperationName());
      op.setSetupTime(req.getSetupTime() != null ? req.getSetupTime() : 0.0);
      op.setRunTime(req.getRunTime() != null ? req.getRunTime() : 0.0);
      op.setQueueTime(req.getQueueTime() != null ? req.getQueueTime() : 0.0);
      operationRepository.save(op);
    }
  }
}
