package com.example.product.service.impl;

import com.example.product.dto.category.CategoryRequest;
import com.example.product.dto.category.CategoryResponse;
import com.example.product.dto.Response;
import com.example.product.dto.category.CategorySearchRequest;
import com.example.product.entity.Category;
import com.example.product.repository.CategoryRepository;
import com.example.product.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Response findAllCategory(){
        log.info("[START - findAllPromo]");

        List<CategoryResponse> categoryResponseList = new ArrayList<>();
        List<Category> categories = categoryRepository.findAll();

        for (Category category : categories){
            CategoryResponse categoryItemResponse = CategoryResponse.builder()
                    .idCategoryProduct(category.getIdCategoryProduct())
                    .categoryCode(category.getCategoryCode().trim())
                    .categoryName(category.getCategoryName())
                    .build();


            categoryResponseList.add(categoryItemResponse);
        }
        Response response = new Response();
        response.setStatus("OK");
        response.setMessage("Success");
        response.setData(categoryResponseList);
        log.info("[END - findAllCategory]");
        return response;
    }

    @Override
    public Response createCategory(CategoryRequest categoryRequest){
        log.info("[START - createCategory]");
        Response response = new Response();

        try{
            Category category = new Category();
            category.setCategoryName(categoryRequest.getCategoryName());
            category.setCategoryCode(categoryRequest.getCategoryCode());

            categoryRepository.save(category);

            response.setStatus("OK");
            response.setStatus("Success");
            response.setData(categoryRequest);

            log.info("[END - createCategory]");
            return response;
        } catch (Exception e) {
            log.error("[createCategory] Error: {}", e.getMessage(), e);
            response.setStatus("INTERNAL_SERVER_ERROR");
            response.setMessage(e.getMessage());
            response.setData(null);
            return response;
        }
    }

    @Override
    public Response updateCategory(CategoryRequest categoryRequest) {
        log.info("[START - updateCategory]");
        Response response = new Response();

        try {
            if (categoryRequest.getIdCategoryProduct() == null){
                throw new RuntimeException("Category ID is required");
            }

//            Category existingCategory = categoryRepository.findByCategoryCode(categoryRequest.getCategoryCode());
            Category existingCategory = categoryRepository
                    .findById(categoryRequest.getIdCategoryProduct())
                    .orElseThrow(() -> new RuntimeException("Category ID Not Found"));

            existingCategory.setCategoryCode(categoryRequest.getCategoryCode());
            existingCategory.setCategoryName(categoryRequest.getCategoryName());

            categoryRepository.save(existingCategory);

            response.setStatus(String.valueOf(HttpStatus.OK));
            response.setMessage("Success");
            response.setData(categoryRequest);
            log.info("[END - updateCategory]");
            return response;
        } catch (Exception e) {
            log.error("[updateCategory] Error: {}", e.getMessage(), e);
            response.setStatus(String.valueOf(HttpStatus.BAD_REQUEST));
            response.setMessage("Failed to update Category");
            response.setData(null);
            return response;
        }
    }

//    @Override
//    public Response searchByCode(CategorySearchRequest categorySearchRequest){
//
//        log.info("[START - searchByCode]");
//        Response response = new Response();
//
//        try {
//            String categoryCode = categorySearchRequest.getCategoryCode();
//
//            Category existingCategory = categoryRepository.findByCategoryCode(categoryCode);
//            if (existingCategory == null) {
//                response.setStatus(String.valueOf(HttpStatus.NOT_FOUND));
//                response.setMessage("Category Code not found");
//                response.setData(null);
//                return response;
//            }
//
//            CategoryResponse categoryItemResponse = CategoryResponse.builder()
//                    .idCategoryProduct(existingCategory.getIdCategoryProduct())
//                    .categoryCode(existingCategory.getCategoryCode().trim())
//                    .categoryName(existingCategory.getCategoryName())
//                    .build();
//
//            response.setStatus(String.valueOf(HttpStatus.OK));
//            response.setMessage("Success");
//            response.setData(List.of(categoryItemResponse));
//
//            log.info("[END - searchByCode]");
//            return response;
//        } catch (Exception e) {
//            log.error("[searchByCode] Error: {}", e.getMessage(), e);
//            response.setStatus(String.valueOf(HttpStatus.BAD_REQUEST));
//            response.setMessage("Failed to update Category");
//            response.setData(null);
//            return response;
//        }
//    }

    @Override
    public Response searchByCode(CategorySearchRequest categorySearchRequest){

        log.info("[START - searchByCode]");
        Response response = new Response();

            String categoryCode = categorySearchRequest.getCategoryCode();

            Category existingCategory = categoryRepository.findByCategoryCode(categoryCode);
            if (existingCategory == null) {
//                response.setStatus(String.valueOf(HttpStatus.NOT_FOUND));
//                response.setMessage("Category Code not found");
//                response.setData(null);
//                return response;
                throw new RuntimeException("Category Code not found");
            }

            CategoryResponse categoryItemResponse = CategoryResponse.builder()
                    .idCategoryProduct(existingCategory.getIdCategoryProduct())
                    .categoryCode(existingCategory.getCategoryCode().trim())
                    .categoryName(existingCategory.getCategoryName())
                    .build();

            response.setStatus(String.valueOf(HttpStatus.OK));
            response.setMessage("Success");
            response.setData(List.of(categoryItemResponse));

            log.info("[END - searchByCode]");
            return response;
    }

    @Override
    public Response deleteCategory() {
        return null;
    }
}
