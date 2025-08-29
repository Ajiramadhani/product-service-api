package com.example.product.service.consumer;

import com.example.product.dto.inventory.InventoryEvent;
import com.example.product.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryConsumer {

    private final InventoryService inventoryService;

    // ✅ Fix: Tambahkan @Payload annotation
    @RabbitListener(queues = "inventory.queue")
    public void processInventoryEvent(@Payload InventoryEvent event) {
        log.info("📨 Received InventoryEvent: {}", event);

        try {
            if (event.getOperation() == null) {
                log.warn("❌ Operation is null in event: {}", event);
                return;
            }

            switch (event.getOperation().toUpperCase()) {
                case "CREATE":
                    inventoryService.createInventoryFromEvent(event);
                    break;
                case "UPDATE":
                case "ADD":
                case "SUBTRACT":
                    inventoryService.updateInventoryFromEvent(event);
                    break;
                case "DELETE":
                    inventoryService.deleteInventoryFromEvent(event);
                    break;
                default:
                    log.warn("❌ Unknown operation: {}", event.getOperation());
            }
        } catch (Exception e) {
            log.error("❌ Error processing event: {}", e.getMessage());
        }
    }
}