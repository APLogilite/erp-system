package com.erp.modules.reservation.service;

import com.erp.modules.reservation.entity.Reservation;
import com.erp.modules.reservation.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

  private final ReservationRepository reservationRepository;

  public ReservationService(ReservationRepository reservationRepository) {
    this.reservationRepository = reservationRepository;
  }

  @Transactional
  public Reservation reserve(Reservation reservation) {
    if (reservation.getQuantity() == null || reservation.getQuantity() <= 0) {
      throw new IllegalArgumentException("Reservation quantity must be positive");
    }
    reservation.setReservedQuantity(reservation.getQuantity());
    reservation.setStatus("RESERVED");
    return reservationRepository.save(reservation);
  }

  @Transactional
  public void release(UUID reservationId) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
    if (!"RESERVED".equals(reservation.getStatus())) {
      throw new IllegalArgumentException("Only RESERVED reservations can be released");
    }
    reservation.setStatus("RELEASED");
    reservation.setReservedQuantity(0.0);
    reservationRepository.save(reservation);
  }

  @Transactional
  public void consume(UUID reservationId, Double quantity) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
    if (!"RESERVED".equals(reservation.getStatus())) {
      throw new IllegalArgumentException("Only RESERVED reservations can be consumed");
    }
    if (quantity > reservation.getReservedQuantity()) {
      throw new IllegalArgumentException("Consume quantity exceeds reserved quantity");
    }
    reservation.setReservedQuantity(reservation.getReservedQuantity() - quantity);
    if (reservation.getReservedQuantity() <= 0) {
      reservation.setStatus("CONSUMED");
    }
    reservationRepository.save(reservation);
  }

  @Transactional
  public void recalculate(UUID productId, UUID warehouseId) {
    List<Reservation> reservations = reservationRepository
        .findByProductIdAndWarehouseId(productId, warehouseId);
    for (Reservation r : reservations) {
      if ("RESERVED".equals(r.getStatus())) {
        r.setReservedQuantity(r.getQuantity());
        reservationRepository.save(r);
      }
    }
  }

  public List<Reservation> getReservationsByProduct(UUID productId) {
    return reservationRepository.findByProductId(productId);
  }

  public List<Reservation> getReservationsByWarehouse(UUID warehouseId) {
    return reservationRepository.findByWarehouseId(warehouseId);
  }

  public Double getTotalReserved(UUID productId, UUID warehouseId) {
    return reservationRepository.findByProductIdAndWarehouseId(productId, warehouseId).stream()
        .filter(r -> "RESERVED".equals(r.getStatus()))
        .mapToDouble(Reservation::getReservedQuantity)
        .sum();
  }
}
