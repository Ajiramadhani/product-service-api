package com.example.product.repository;

import com.example.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByCodeProduct(String codeProduct);
    boolean existsById(Long id);

//    @Query(value = "SELECT product_code, category_code,product_name, product_price, product_thumbnail, product_description, is_deleted FROM product WHERE is_deleted = FALSE AND product_price BETWEEN ")
    public List<Product> findByPriceBetweenAndIsDeletedFalse(Double min, Double max);
    public Product findByCodeProduct(String codeProduct);

}
