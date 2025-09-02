package com.example.product.dto.product;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearch {
    @NotBlank(message = "Product code wajib diisi")
    private String codeProduct;
}
