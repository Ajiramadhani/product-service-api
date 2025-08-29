package com.example.product.service.impl;

import com.example.product.dto.inventory.InventoryEvent;
import com.example.product.dto.inventory.InventoryRequest;
import com.example.product.dto.inventory.InventoryResponse;
import com.example.product.entity.Inventory;
import com.example.product.repository.InventoryRepository;
import com.example.product.repository.ProductRepository;
import com.example.product.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    
//    @Override
//    @Transactional
//    public InventoryResponse createInventory(InventoryRequest request) {
//        log.info("[START] createInventory - productId: {}", request.getProductId());
//
//        if (inventoryRepository.existsByProductId(request.getProductId())){
//            throw new RuntimeException("Inventory already exists for product ID: "+ request.getProductId());
//        }
//
//        Inventory inventory = new Inventory();
//        inventory.setIdInventory(request.getProductId());
//        inventory.setQuantity(request.getQuantity());
//
//        Inventory savedInventory = inventoryRepository.save(inventory);
//        log.info("[END] createInventory - productId: {}", request.getProductId());
//        return convertToResponse(savedInventory);
//    }

    @Override
    @Transactional
    public InventoryResponse createInventory(InventoryRequest request) {
        log.info("[START] createInventory - productId: {}", request.getProductId());

        try {
            // Cek apakah product exists
            if (!productRepository.existsById(request.getProductId())) {
                throw new RuntimeException("Product with ID " + request.getProductId() + " does not exist");
            }

            if (inventoryRepository.existsByProductId(request.getProductId())) {
                throw new RuntimeException("Inventory already exists for product ID: " + request.getProductId());
            }

            Inventory inventory = new Inventory();
            inventory.setProductId(request.getProductId());
            inventory.setQuantity(request.getQuantity());

            Inventory savedInventory = inventoryRepository.save(inventory);

            log.info("[END] createInventory - success for productId: {}", request.getProductId());
            return convertToResponse(savedInventory);
        } catch (Exception e) {
            log.error("Error creating inventory: {}", e.getMessage());
            throw e; // Re-throw untuk ditangani controller
        }
    }

    private InventoryResponse convertToResponse(Inventory inventory) {
        return  InventoryResponse.builder()
                .id(inventory.getIdInventory())
                .productId(inventory.getProductId())
                .quantity(inventory.getQuantity())
                .reserved(inventory.getReserved())
                .availableStock(inventory.getAvailableStock())
                .lowStock(inventory.isLowStock())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    @Override
    public InventoryResponse getInventoryByProductId(Long productId) {
        log.info("Getting inventory for product ID: {}", productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product ID: " + productId));

        return convertToResponse(inventory);
    }

    @Override
    public InventoryResponse updateInventory(InventoryRequest request) {
        log.info("Updating inventory for product ID: {}", request.getProductId());

        Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Inventory not found for product ID : "+ request.getProductId()));

        if ("ADD".equalsIgnoreCase(request.getOperation())){
            inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
        } else if ("SUBTRACT".equalsIgnoreCase(request.getOperation())) {
            if (inventory.getAvailableStock() < request.getQuantity()){
                throw new RuntimeException("Insufficient stock for product ID: " + request.getProductId());
            }
            inventory.setQuantity(inventory.getQuantity() - request.getQuantity());
        } else {
            inventory.setQuantity(request.getQuantity());
        }

        Inventory updatedInventory = inventoryRepository.save(inventory);
        log.info("Inventory updated for product ID: {}", request.getProductId());
        return convertToResponse(updatedInventory);
    }

    @Override
    public List<InventoryResponse> getAllInventory() {
        log.info("Getting all inventory");

        return inventoryRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InventoryResponse reserveStock(Long productId, Integer quantity) {
        log.info("Reserving {} units for product ID: {}", quantity, productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(()-> new RuntimeException("Inventory not found for product ID: "+productId));

        if (inventory.getAvailableStock() < quantity){
            throw new RuntimeException("Insufficient available stock for product ID: " + productId);
        }

        inventory.setReserved(inventory.getReserved() + quantity);
        Inventory updatedInventory = inventoryRepository.save(inventory);

        log.info("Stock reserved successfully for product ID: {}", productId);
        return convertToResponse(updatedInventory);
    }

    @Override
    @Transactional
    public InventoryResponse releaseReservation(Long productId, Integer quantity) {
        log.info("Releasing {} units reservation for product ID: {}", quantity, productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product ID: " + productId));

        if (inventory.getReserved() < quantity) {
            throw new RuntimeException("Cannot release more than reserved quantity for product ID: " + productId);
        }

        inventory.setReserved(inventory.getReserved() - quantity);
        Inventory updatedInventory = inventoryRepository.save(inventory);

        log.info("Reservation released successfully for product ID: {}", productId);
        return convertToResponse(updatedInventory);
    }

    @Override
    @Transactional
    public void createInventoryFromEvent(InventoryEvent event) {
        log.info("[EVENT] Creating inventory from event for product ID: {}", event.getProductId());

        try {
            // Cek apakah product exists
            if (!productRepository.existsById(event.getProductId())) {
                log.error("❌ Product not found: {}", event.getProductId());
                return;
            }

            // Cek apakah inventory sudah ada
            if (inventoryRepository.existsByProductId(event.getProductId())) {
                log.info("⚠️ Inventory already exists for product ID: {}", event.getProductId());
                return;
            }

            Inventory inventory = new Inventory();
            inventory.setProductId(event.getProductId());
            inventory.setQuantity(event.getQuantity() != null ? event.getQuantity() : 0);

            Inventory savedInventory = inventoryRepository.save(inventory);
            log.info("✅ Inventory created from event for product ID: {}", event.getProductId());

        } catch (Exception e) {
            log.error("❌ Error creating inventory from event: {}", e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateInventoryFromEvent(InventoryEvent event) {
        log.info("[EVENT] Updating inventory from event for product ID: {}", event.getProductId());

        try {
            Inventory inventory = inventoryRepository.findByProductId(event.getProductId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found for product ID: " + event.getProductId()));

            if ("ADD".equalsIgnoreCase(event.getOperation())) {
                inventory.setQuantity(inventory.getQuantity() + event.getQuantity());
            } else if ("SUBTRACT".equalsIgnoreCase(event.getOperation())) {
                if (inventory.getAvailableStock() < event.getQuantity()) {
                    log.error("❌ Insufficient stock for product ID: {}", event.getProductId());
                    return;
                }
                inventory.setQuantity(inventory.getQuantity() - event.getQuantity());
            } else {
                // SET operation (default)
                inventory.setQuantity(event.getQuantity());
            }

            inventoryRepository.save(inventory);
            log.info("✅ Inventory updated from event for product ID: {}", event.getProductId());

        } catch (Exception e) {
            log.error("❌ Error updating inventory from event: {}", e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteInventoryFromEvent(InventoryEvent event) {
        log.info("[EVENT] Deleting inventory from event for product ID: {}", event.getProductId());

        try {
            Inventory inventory = inventoryRepository.findByProductId(event.getProductId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found for product ID: " + event.getProductId()));

            inventoryRepository.delete(inventory);
            log.info("✅ Inventory deleted from event for product ID: {}", event.getProductId());

        } catch (Exception e) {
            log.error("❌ Error deleting inventory from event: {}", e.getMessage());
        }
    }
}
