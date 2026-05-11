package com.erp.modules.order.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Order Request DTO.
 */
public class OrderRequestDTO {

    @NotNull
    private String orderType; // SALES / PURCHASE

    @NotNull
    private UUID partyId; // customerId or vendorId

    @NotNull
    private LocalDateTime orderDate;

    private String status; // DRAFT / CONFIRMED / COMPLETED / CANCELLED

    @NotNull
    private List<OrderLineRequestDTO> lines; // Order line items

    // Getters and setters
    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public UUID getPartyId() {
        return partyId;
    }

    public void setPartyId(UUID partyId) {
        this.partyId = partyId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderLineRequestDTO> getLines() {
        return lines;
    }

    public void setLines(List<OrderLineRequestDTO> lines) {
        this.lines = lines;
    }
}
