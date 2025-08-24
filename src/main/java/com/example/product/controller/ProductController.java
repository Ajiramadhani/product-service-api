package com.example.product.controller;

import com.example.product.dto.Response;
import com.example.product.dto.product.ProductRequest;
import com.example.product.service.ProductService;
import com.example.product.service.RedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private RedisService redisService;

    private static final String ALL_PRODUCTS_CACHE_KEY = "all_product";
    private static final String CACHE_STATS_KEY = "cache_status";

    @GetMapping("/getAll")
    @Operation(summary = "Get All Product Request", description = "Retriever a list of all Products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Response> findAllProducts(HttpServletRequest httpServletRequest){
        log.info("HTTP REQUEST ALL PRODUCTS");

        Object cachedData = redisService.getValue(ALL_PRODUCTS_CACHE_KEY);
        if (cachedData != null){
            log.info("Returning cached products data");
            Response cachedResponse = (Response) cachedData;
            cachedResponse.setMessage("Success (from redis)");
            return ResponseEntity.status(HttpStatus.OK).body(cachedResponse);
        }
        Response response = productService.findAllProduct();

        redisService.setValue(ALL_PRODUCTS_CACHE_KEY, response, 1, TimeUnit.HOURS);
        log.info("HTTP RESPONSE : {}", HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/createProduct")
    @Operation(summary = "Create a new Product Request", description = "Create a new Data Product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Response> createProduct(@Valid @RequestBody ProductRequest request){
        log.info("HTTP CREATE NEW PRODUCTS");
        Response response = productService.createProduct(request);

        redisService.deleteValue(ALL_PRODUCTS_CACHE_KEY);
        log.info("Cache invalidated after product created");
        log.info("HTTP RESPONSE : {}", HttpStatus.OK);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cache/stats")
    @Operation(summary = "Get cache statistics", description = "Get Redis cache statistics and information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Response> getCacheStast(){
        log.info("HTTP REQUEST CACHE STATS");

        Map<String, Object> cacheData = new HashMap<>();
        cacheData.put("cacheEnabled", true);
        cacheData.put("allProductsCached", redisService.hasKey(ALL_PRODUCTS_CACHE_KEY));
        cacheData.put("redisStatus", "Connected");
        cacheData.put("timestamp", new Date());

        Response response = new Response();
        response.setStatus("OK");
        response.setMessage("Cache statistics retrieved successfully");
        response.setData(cacheData);

        log.info("HTTP RESPONSE : {}", HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/cache/clear")
    @Operation(summary = "Clear product cache", description = "Clear all product-related cache entries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully cleared"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Response> clearProductCache(){
        log.info("HTTP REQUEST CLEAR CACHE");
        redisService.deleteValue(ALL_PRODUCTS_CACHE_KEY);
        Map<String, Object> result = new HashMap<>();
        result.put("cleared", true);
        result.put("cacheKey", ALL_PRODUCTS_CACHE_KEY);
        result.put("timestamp", System.currentTimeMillis());

        Response response = new Response();
        response.setStatus("OK");
        response.setMessage("Product data cache cleared successfully");
        response.setData(result);

        log.info("HTTP RESPONSE : {}", HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/health")
    @Operation(summary = "Health check with cache", description = "Health check endpoint that uses caching")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service is healthy"),
            @ApiResponse(responseCode = "500", description = "Service unavailable")
    })
    public ResponseEntity<Response> healthCheck() {
        log.info("HTTP REQUEST HEALTH CHECK");

        // Simple health check with caching
        Object cachedHealth = redisService.getValue("health_check");
        Map<String, Object> healthData;

        if (cachedHealth != null) {
            healthData = (Map<String, Object>) cachedHealth;
            healthData.put("source", "cache");
        } else {
            healthData = new HashMap<>();
            healthData.put("status", "healthy");
            healthData.put("timestamp", new Date());
            healthData.put("service", "product-service");
            healthData.put("source", "database");

            // Cache health check for 30 seconds
            redisService.setValue("health_check", healthData, 30, java.util.concurrent.TimeUnit.SECONDS);
        }

        Response response = new Response();
        response.setStatus(String.valueOf(HttpStatus.OK.value()));
        response.setMessage("Service is healthy");
        response.setData(healthData);

        log.info("HTTP RESPONSE : {}", HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
