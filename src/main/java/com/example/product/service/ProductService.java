package com.example.product.service;

import com.example.product.dto.Response;
import com.example.product.dto.category.CategoryRequest;
import com.example.product.dto.product.ProductFilter;
import com.example.product.dto.product.ProductRequest;
import com.example.product.dto.product.ProductSearch;

public interface ProductService {

    Response findAllProduct();
    Response createProduct(ProductRequest productRequest);
    Response updateProduct(ProductRequest productRequest);
    Response deleteProduct();
    Response searchByCodeProduct(ProductSearch productSearch);
    Response filterProduct(ProductFilter productFilter);
}
