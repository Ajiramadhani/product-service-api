package com.example.product.service;

import com.example.product.dto.Response;
import com.example.product.dto.category.CategoryRequest;
import com.example.product.dto.product.ProductRequest;

public interface ProductService {

    Response findAllProduct();
    Response createProduct(ProductRequest productRequest);
    Response updateProduct(ProductRequest productRequest);
    Response deleteProduct();
    Response searchByCodeProduct(CategoryRequest productRequest);
}
