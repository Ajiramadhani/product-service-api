package com.example.product.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "inventories")
@EqualsAndHashCode(callSuper = false)
@EntityListeners(EntityListeners.class)
public class Inventory extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventory")
    private Long idInventory;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id", referencedColumnName = "id_product", insertable = false, updatable = false)
//    private Product product;
//    @Column(name = "product_id", nullable = false)
//    private Long productId;
    @Column(name = "product_id", unique = true, nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(nullable = false)
    private Integer reserved = 0;

    @Column(name = "min_stock_level")
    private Integer minStockLevel = 10;

    // Available stock calculation
    public Integer getAvailableStock() {
        return quantity - reserved;
    }

    // Check if stock is low
    public Boolean isLowStock() {
        return getAvailableStock() <= minStockLevel;
    }

}
