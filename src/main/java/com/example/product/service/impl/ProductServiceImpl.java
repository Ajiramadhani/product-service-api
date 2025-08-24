package com.example.product.service.impl;

import com.example.product.dto.Response;
import com.example.product.dto.category.CategoryRequest;
import com.example.product.dto.category.CategoryResponse;
import com.example.product.dto.product.ProductRequest;
import com.example.product.dto.product.ProductResponse;
import com.example.product.entity.Category;
import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;
import com.example.product.service.ProductService;
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
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;


    @Override
    public Response findAllProduct() {
        log.info("[START - findAllProduct]");
        List<ProductResponse> productResponseList = new ArrayList<>();
        List<Product> products = productRepository.findAll();

        for (Product product: products){
            ProductResponse productResponse = ProductResponse.builder()
                    .idProduct(product.getProductId())
                    .productName(product.getProductName())
                    .productCode(product.getCodeProduct())
                    .categoryCode(product.getCategoryCode())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .photoProduct(product.getPhotoProduct())
                    .build();

            productResponseList.add(productResponse);
        }
        Response response = new Response();
        response.setStatus(String.valueOf(HttpStatus.OK));
        response.setMessage("Success");
        response.setData(productResponseList);
        log.info("[END - findAllProduct]");
        return response;
    }

    @Override
    public Response createProduct(ProductRequest productRequest) {
        log.info("[START - createProduct]");
        Response response = new Response();

        try {
            Product product = new Product();
            product.setCodeProduct(productRequest.getCodeProduct());
            product.setProductName(productRequest.getProductName());
            product.setDescription(productRequest.getDescription());
            product.setPrice(productRequest.getPrice());
            product.setPhotoProduct(product.getPhotoProduct());
            product.setThumbnailProduct(product.getThumbnailProduct());
            product.setCategoryCode(productRequest.getCategoryCode());

            productRepository.save(product);

            response.setStatus(String.valueOf(HttpStatus.OK));
            response.setMessage("Success");
            response.setData(productRequest);
            log.info("[END - createProduct]");
            return response;
        } catch (Exception ex) {
            log.error("[createProduct] Error : {}", ex.getMessage(), ex);
            response.setStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR));
            response.setMessage(ex.getMessage());
            response.setData(null);
            return response;
        }
    }

    @Override
    public Response updateProduct(ProductRequest productRequest) {
        log.info("[START - updateProduct]");
        log.info("[END - updateProduct]");
        return null;
    }

    @Override
    public Response deleteProduct() {
        return null;
    }

    @Override
    public Response searchByCodeProduct(CategoryRequest productRequest) {
        log.info("[START - searchByCodeProduct]");
        log.info("[END - searchByCodeProduct]");
        return null;
    }
}
