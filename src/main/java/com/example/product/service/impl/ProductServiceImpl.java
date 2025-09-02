package com.example.product.service.impl;

import com.example.product.dto.Response;
import com.example.product.dto.inventory.InventoryEvent;
import com.example.product.dto.product.ProductFilter;
import com.example.product.dto.product.ProductRequest;
import com.example.product.dto.product.ProductResponse;
import com.example.product.dto.product.ProductSearch;
import com.example.product.entity.Product;
import com.example.product.exception.ProductExceptionHandler;
import com.example.product.repository.ProductRepository;
import com.example.product.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    @Autowired
    private RabbitTemplate rabbitTemplate;


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
            // Validasi input
            if (productRequest.getCodeProduct() == null || productRequest.getCodeProduct().isEmpty()) {
                throw new RuntimeException("Product code is required");
            }

            // Cek duplicate product code
            if (productRepository.existsByCodeProduct(productRequest.getCodeProduct())) {
                throw new RuntimeException("Product with code " + productRequest.getCodeProduct() + " already exists");
            }

            Product product = new Product();
            product.setCodeProduct(productRequest.getCodeProduct());
            product.setProductName(productRequest.getProductName());
            product.setDescription(productRequest.getDescription());
            product.setPrice(productRequest.getPrice());
            product.setPhotoProduct(productRequest.getPhotoProduct());
            product.setThumbnailProduct(productRequest.getThumbnailProduct());
            product.setCategoryCode(productRequest.getCategoryCode());

            Product savedProduct = productRepository.save(product);

            // Send inventory creation event to RabbitMQ
            try {
                InventoryEvent event = new InventoryEvent(
                        savedProduct.getProductId(), // Pastikan gunakan field yang benar
                        0,  // Initial quantity 0
                        "CREATE"
                );

                rabbitTemplate.convertAndSend(
                        "inventory.exchange",
                        "inventory.update",
                        event
                );

                log.info("Inventory event sent for product ID: {}", savedProduct.getProductId());
            } catch (Exception e) {
                log.warn("Failed to send RabbitMQ event: {}", e.getMessage());
                // Jangan throw error, continue proses
            }

            response.setStatus(String.valueOf(HttpStatus.OK.value()));
            response.setMessage("Product created successfully");
            response.setData(convertToProductResponse(savedProduct));

            log.info("[END - createProduct] - Product created: {}", savedProduct.getCodeProduct());
            return response;
        } catch (Exception ex) {
            log.error("[createProduct] Error: {}", ex.getMessage(), ex);
            response.setStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
            response.setMessage(ex.getMessage());
            response.setData(null);
            return response;
        }
    }

    private ProductResponse convertToProductResponse(Product product) {
        return ProductResponse.builder()
                .idProduct(product.getProductId())
                .productName(product.getProductName())
                .productCode(product.getCodeProduct())
                .categoryCode(product.getCategoryCode())
                .description(product.getDescription())
                .price(product.getPrice())
                .photoProduct(product.getPhotoProduct())
                .build();
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
    public Response searchByCodeProduct(ProductSearch productSearch) {
        log.info("[START - searchByCodeProduct]");
        Response response = new Response();

        if (productSearch.getCodeProduct() == null) {
            throw  new ProductExceptionHandler("Kode Produk wajib diisi", "codeProduct is Null", HttpStatus.BAD_REQUEST);
        }

        Product product = productRepository.findByCodeProduct(productSearch.getCodeProduct());
        ProductResponse productResponse = new ProductResponse().builder()
                .productName(product.getProductName())
                .productCode(product.getCodeProduct())
                .description(product.getDescription())
                .thumbnail(product.getThumbnailProduct())
                .price(product.getPrice())
                .build();

        response.setStatus(String.valueOf(HttpStatus.OK));
        response.setMessage("Success");
        response.setData(List.of(productResponse));

        log.info("[END - searchByCodeProduct]");
        return response;
    }

    @Override
    public Response filterProduct(ProductFilter productFilter) {
        log.info("[START - filterProduct]");

        Response response = new Response();

        try {
            if (productFilter.getMin() == null || productFilter.getMax() == null){
                throw new ProductExceptionHandler("Invalid filter parameters", "Parameter min, max, or isDeleted cannot be null", HttpStatus.BAD_REQUEST);
            }

        List<Product> products = productRepository.findByPriceBetweenAndIsDeletedFalse(productFilter.getMin(), productFilter.getMax());
            response.setStatus(String.valueOf(HttpStatus.OK));
            response.setMessage("Success");
            response.setData(products);
        } catch (ProductExceptionHandler e){
            log.error("ProductExceptionHandler: {}", e.getMessage());
            response.setStatus(String.valueOf(e.getHttpStatus().value()));
            response.setMessage(e.getMessage());
            response.setData(null);
        }catch (Exception e) {
            log.error("Unexpected error: ", e);
            response.setStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
            response.setMessage("Internal server error");
            response.setData(null);
        }
        log.info("[END - filterProduct]");
        return response;
    }
}
