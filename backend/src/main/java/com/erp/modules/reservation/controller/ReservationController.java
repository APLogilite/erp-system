package com.erp.modules.reservation.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.reservation.dto.ReservationRequest;
import com.erp.modules.reservation.dto.ReservationResponse;
import com.erp.modules.reservation.entity.Reservation;
import com.erp.modules.reservation.service.ReservationService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/reservations")
public class ReservationController {

  private final ReservationService reservationService;

  public ReservationController(ReservationService reservationService) {
    this.reservationService = reservationService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<ReservationResponse>> create(@RequestBody ReservationRequest request) {
    Reservation reservation = new Reservation();
    reservation.setProductId(request.getProductId());
    reservation.setWarehouseId(request.getWarehouseId());
    reservation.setLocationId(request.getLocationId());
    reservation.setQuantity(request.getQuantity());
    reservation.setSourceDocument(request.getSourceDocument());
    reservation.setSourceLine(request.getSourceLine());
    Reservation saved = reservationService.reserve(reservation);
    return ResponseEntity.ok(ApiResponse.success(toResponse(saved), "Reservation created"));
  }

  @PostMapping("/{id}/release")
  public ResponseEntity<ApiResponse<Void>> release(@PathVariable UUID id) {
    reservationService.release(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Reservation released"));
  }

  @PostMapping("/{id}/consume")
  public ResponseEntity<ApiResponse<Void>> consume(@PathVariable UUID id, @RequestBody Double quantity) {
    reservationService.consume(id, quantity);
    return ResponseEntity.ok(ApiResponse.successMessage("Reservation consumed"));
  }

  @GetMapping("/product/{productId}/warehouse/{warehouseId}")
  public ResponseEntity<ApiResponse<List<ReservationResponse>>> getByProductAndWarehouse(
      @PathVariable UUID productId, @PathVariable UUID warehouseId) {
    List<Reservation> reservations = reservationService.getReservationsByProduct(productId).stream()
        .filter(r -> r.getWarehouseId().equals(warehouseId))
        .collect(Collectors.toList());
    List<ReservationResponse> list = reservations.stream().map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Reservations retrieved"));
  }

  @GetMapping("/product/{productId}/warehouse/{warehouseId}/total")
  public ResponseEntity<ApiResponse<Double>> getTotalReserved(
      @PathVariable UUID productId, @PathVariable UUID warehouseId) {
    Double total = reservationService.getTotalReserved(productId, warehouseId);
    return ResponseEntity.ok(ApiResponse.success(total, "Total reserved retrieved"));
  }

  private ReservationResponse toResponse(Reservation r) {
    ReservationResponse resp = new ReservationResponse();
    resp.setId(r.getId());
    resp.setProductId(r.getProductId());
    resp.setWarehouseId(r.getWarehouseId());
    resp.setLocationId(r.getLocationId());
    resp.setQuantity(r.getQuantity());
    resp.setReservedQuantity(r.getReservedQuantity());
    resp.setSourceDocument(r.getSourceDocument());
    resp.setSourceLine(r.getSourceLine());
    resp.setStatus(r.getStatus());
    resp.setCreatedAt(r.getCreatedAt());
    resp.setUpdatedAt(r.getUpdatedAt());
    resp.setIsActive(r.getIsActive());
    return resp;
  }
}
