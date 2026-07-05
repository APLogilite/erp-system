package com.erp.modules.reservation.repository;

import com.erp.modules.reservation.entity.Reservation;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
  List<Reservation> findByProductId(UUID productId);
  List<Reservation> findByWarehouseId(UUID warehouseId);
  List<Reservation> findByProductIdAndWarehouseId(UUID productId, UUID warehouseId);
  List<Reservation> findByStatus(String status);
  List<Reservation> findBySourceDocument(String sourceDocument);
}
