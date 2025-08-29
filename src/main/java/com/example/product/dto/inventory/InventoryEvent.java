package com.example.product.dto.inventory;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class InventoryEvent {
    private Long productId;
    private Integer quantity;
    private String operation; // CREATE, UPDATE, RESERVE, RELEASE
    private Date timestamp;

    public InventoryEvent(Long productId, Integer quantity, String operation) {
        this.productId = productId;
        this.quantity = quantity;
        this.operation = operation;
        this.timestamp = new Date();
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
