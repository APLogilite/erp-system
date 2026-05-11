package com.erp.modules.order.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Order entity (header/document).
 * Unified order system for SALES and PURCHASE orders.
 */
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String orderNumber; // unique business identifier

    @NotNull
    @Column(nullable = false)
    private String orderType; // SALES / PURCHASE

    @NotNull
    @Column(nullable = false)
    private UUID partyId; // customerId or vendorId

    @NotNull
    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false)
    private String status = "DRAFT"; // DRAFT / CONFIRMED / COMPLETED / CANCELLED

    @Column(nullable = false)
    private Double totalAmount;

    // Getters and setters
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

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

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
