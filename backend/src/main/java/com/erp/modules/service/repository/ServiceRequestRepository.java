package com.erp.modules.service.repository;

import com.erp.modules.service.entity.ServiceRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, UUID> {
  Optional<ServiceRequest> findByTicketNumber(String ticketNumber);
  List<ServiceRequest> findByCustomerId(UUID customerId);
  List<ServiceRequest> findByAssignedEngineerId(UUID engineerId);
  List<ServiceRequest> findByStatus(String status);
}
