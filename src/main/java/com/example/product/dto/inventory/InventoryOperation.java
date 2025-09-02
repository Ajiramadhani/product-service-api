package com.example.product.dto.inventory;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryOperation {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    private String operation; // ADD, SUBTRACT, SET
}
