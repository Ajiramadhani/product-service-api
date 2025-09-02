package com.example.product.dto.inventory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class BulkInventoryRequest {

    @NotNull(message = "Operations list is required")
    @Valid
    private List<InventoryOperation> operations;
}

