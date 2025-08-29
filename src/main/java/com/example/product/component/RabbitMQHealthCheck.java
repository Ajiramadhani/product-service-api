package com.example.product.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQHealthCheck implements CommandLineRunner {

    private final ConnectionFactory connectionFactory;

    @Override
    public void run(String... args) {
        try {
            // Test connection
            var connection = connectionFactory.createConnection();
            var isOpen = connection.isOpen();
            connection.close();

            if (isOpen) {
                log.info("✅ RabbitMQ connection successful");
            } else {
                log.error("❌ RabbitMQ connection failed");
            }
        } catch (Exception e) {
            log.error("❌ RabbitMQ connection error: {}", e.getMessage());
        }
    }
}
