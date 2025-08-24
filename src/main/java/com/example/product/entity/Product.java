package com.example.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PRODUCT")
@EntityListeners(EntityListeners.class)
public class Product extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_product")
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_price", nullable = false)
    private Double price;

    @Column(name = "product_description")
    private String description;

    @Column(name = "product_pic")
    private String photoProduct;

    @Column(name = "product_thumbnail")
    private String thumbnailProduct;

    @Column(name = "product_code", nullable = false)
    private String codeProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_code", referencedColumnName = "category_code", insertable = false, updatable = false)
    private Category category;
    @Column(name = "category_code", nullable = false)
    private String categoryCode;
}
