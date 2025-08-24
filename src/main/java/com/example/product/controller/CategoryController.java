package com.example.product.controller;

import com.example.product.dto.category.CategoryRequest;
import com.example.product.dto.Response;
import com.example.product.dto.category.CategorySearchRequest;
import com.example.product.service.CategoryService;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getAll")
    @Operation(summary = "Get All Categories Product", description = "Retrieve a list of all categories products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Response> findALlCategories(HttpServletRequest httpServletRequest){
        log.info("HTTP REQUEST ALL CATEGORIES");
        Response response = categoryService.findAllCategory();
        log.info("HTTP RESPONSE : {}", HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/addCategories")
    @Operation(summary = "Add New Category", description = "Add new Category Product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Response> create(@Valid @RequestBody CategoryRequest request){
        Response response = categoryService.createCategory(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/updateCategories")
    @Operation(summary = "Update Category", description = "Update Category Product by Id Category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Response> update(@Valid @RequestBody CategoryRequest request){
        Response response = categoryService.updateCategory(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/searchCode")
    @Operation(summary = "Search by Code Category", description = "Search Category by Code Category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Response> searchByCode(@RequestBody CategorySearchRequest request){
        Response response = categoryService.searchByCode(request);
        return ResponseEntity.ok(response);

    }

//    public ResponseEntity<Response> searchByCode(@RequestParam String categoryCode){
//        Response response = categoryService.searchByCode(categoryCode);
//        return ResponseEntity.ok(response);
//    }
}
