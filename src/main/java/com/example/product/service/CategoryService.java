package com.example.product.service;

import com.example.product.dto.category.CategoryRequest;
import com.example.product.dto.Response;
import com.example.product.dto.category.CategorySearchRequest;
import jakarta.validation.Valid;

public interface CategoryService {
    Response findAllCategory();

    Response createCategory(CategoryRequest categoryRequest);
    Response updateCategory(CategoryRequest categoryRequest);
    Response deleteCategory();
    Response searchByCode(CategorySearchRequest categorySearchRequest);
}
