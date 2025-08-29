package com.example.product.controller;

import com.example.product.dto.Response;
import com.example.product.dto.inventory.InventoryRequest;
import com.example.product.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/create")
    @Operation(summary = "Create inventory", description = "Create inventory for a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Inventory already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Response> createInventory(@Valid @RequestBody InventoryRequest request) {
        log.info("HTTP CREATE INVENTORY for product ID: {}", request.getProductId());

        Response response = new Response();
        try {
            var inventoryResponse = inventoryService.createInventory(request);

            response.setStatus(String.valueOf(HttpStatus.OK.value()));
            response.setMessage("Inventory created successfully");
            response.setData(inventoryResponse);

            log.info("HTTP RESPONSE : {}", HttpStatus.OK);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error creating inventory: {}", e.getMessage());

            // Handle specific errors
            if (e.getMessage().contains("already exists")) {
                response.setStatus(String.valueOf(HttpStatus.CONFLICT.value()));
                response.setMessage(e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            } else if (e.getMessage().contains("does not exist")) {
                response.setStatus(String.valueOf(HttpStatus.NOT_FOUND.value()));
                response.setMessage(e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                response.setStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));
                response.setMessage(e.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            log.error("Unexpected error creating inventory: {}", e.getMessage());

            response.setStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
            response.setMessage("Internal server error");
            response.setData(null);

            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get inventory by product ID", description = "Get inventory details for a specific product")
    public ResponseEntity<Response> getInventoryByProductId(@PathVariable Long productId) {
        log.info("HTTP GET INVENTORY for product ID: {}", productId);

        Response response = new Response();
        try {
            var inventoryResponse = inventoryService.getInventoryByProductId(productId);

            response.setStatus(String.valueOf(HttpStatus.OK.value()));
            response.setMessage("Inventory retrieved successfully");
            response.setData(inventoryResponse);

            log.info("HTTP RESPONSE : {}", HttpStatus.OK);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting inventory: {}", e.getMessage());

            response.setStatus(String.valueOf(HttpStatus.NOT_FOUND.value()));
            response.setMessage(e.getMessage());
            response.setData(null);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/update")
    @Operation(summary = "Update inventory", description = "Update inventory quantity")
    public ResponseEntity<Response> updateInventory(@Valid @RequestBody InventoryRequest request) {
        log.info("HTTP UPDATE INVENTORY for product ID: {}", request.getProductId());

        Response response = new Response();
        try {
            var inventoryResponse = inventoryService.updateInventory(request);

            response.setStatus(String.valueOf(HttpStatus.OK.value()));
            response.setMessage("Inventory updated successfully");
            response.setData(inventoryResponse);

            log.info("HTTP RESPONSE : {}", HttpStatus.OK);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating inventory: {}", e.getMessage());

            response.setStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));
            response.setMessage(e.getMessage());
            response.setData(null);

            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Get all inventory", description = "Get all inventory records")
    public ResponseEntity<Response> getAllInventory() {
        log.info("HTTP GET ALL INVENTORY");

        Response response = new Response();
        try {
            var inventoryList = inventoryService.getAllInventory();

            response.setStatus(String.valueOf(HttpStatus.OK.value()));
            response.setMessage("All inventory retrieved successfully");
            response.setData(inventoryList);

            log.info("HTTP RESPONSE : {}", HttpStatus.OK);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting all inventory: {}", e.getMessage());

            response.setStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
            response.setMessage(e.getMessage());
            response.setData(null);

            return ResponseEntity.internalServerError().body(response);
        }
    }
}
