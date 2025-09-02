package com.example.product.dto.product;

import com.example.product.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse extends BaseEntity {
    private Long idProduct;
    private String productName;
    private String productCode;
    private String description;
    private String thumbnail;
    private Double price;
    private String photoProduct;
    private String categoryCode;
}
