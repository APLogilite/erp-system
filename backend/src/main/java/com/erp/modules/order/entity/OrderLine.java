package com.erp.modules.order.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

/**
 * OrderLine entity.
 * Represents individual line items in an order.
 */
@Entity
@Table(name = "order_lines")
public class OrderLine extends BaseEntity {

    @NotNull
    @Column(nullable = false)
    private UUID orderId;

    @NotNull
    @Column(nullable = false)
    private UUID productId;

    @Positive
    @NotNull
    @Column(nullable = false)
    private Double quantity;

    @NotNull
    @Column(nullable = false)
    private Double unitPrice;

    @NotNull
    @Column(nullable = false)
    private Double lineTotal; // quantity * unitPrice

    // Getters and setters
    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(Double lineTotal) {
        this.lineTotal = lineTotal;
    }
}
