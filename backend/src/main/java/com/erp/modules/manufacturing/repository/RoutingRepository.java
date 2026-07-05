package com.erp.modules.manufacturing.repository;

import com.erp.modules.manufacturing.entity.Routing;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutingRepository extends JpaRepository<Routing, UUID> {
  Optional<Routing> findByCode(String code);
}
