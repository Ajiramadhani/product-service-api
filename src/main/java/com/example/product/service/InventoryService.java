package com.example.product.service;

import com.example.product.dto.inventory.InventoryEvent;
import com.example.product.dto.inventory.InventoryRequest;
import com.example.product.dto.inventory.InventoryResponse;

import java.util.List;

public interface InventoryService {

    InventoryResponse createInventory(InventoryRequest request);
    InventoryResponse getInventoryByProductId(Long productId);
    InventoryResponse updateInventory(InventoryRequest request);
    List<InventoryResponse> getAllInventory();
    InventoryResponse reserveStock(Long productId, Integer quantity);
    InventoryResponse releaseReservation(Long productId, Integer quantity);

    void createInventoryFromEvent(InventoryEvent event);
    void updateInventoryFromEvent(InventoryEvent event);
    void deleteInventoryFromEvent(InventoryEvent event);
}
