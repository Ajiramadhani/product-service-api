package com.example.product.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategorySearchRequest {
    @NotBlank(message = "Category code is required")
    private String categoryCode;
}
