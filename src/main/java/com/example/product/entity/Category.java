package com.example.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CATEGORY_PRODUCT")
@EntityListeners(EntityListeners.class)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_category_product")
    private Long idCategoryProduct;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "category_code", length = 10, unique = true)
    private String categoryCode;
}
