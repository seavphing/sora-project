package com.soramitsu.assignment.wallet_service.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soramitsu.assignment.wallet_service.dto.CreateWalletRequest;
import com.soramitsu.assignment.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventListener {

    private final WalletService walletService;
    private final ObjectMapper objectMapper;

    /**
     * Listen for user created events to create a wallet for new users
     *
     * @param message JSON message from RabbitMQ
     */
    @RabbitListener(queues = "${rabbitmq.queue.user-created}")
    public void handleUserCreatedEvent(String message) {
        try {
            log.info("Received user created event: {}", message);

            // Parse message to get user details
            Map<String, Object> event = objectMapper.readValue(message, Map.class);

            Long userId = Long.valueOf(event.get("userId").toString());
            String defaultCurrency = (String) event.get("defaultCurrency");
            String username = (String) event.get("username");

            log.info("Creating wallet for new user: {} (ID: {}), currency: {}", username, userId, defaultCurrency);

            // Create wallet for user
            CreateWalletRequest request = new CreateWalletRequest();
            request.setUserId(userId);
            request.setCurrencyCode(defaultCurrency);

            walletService.createWallet(request);

            log.info("Wallet created successfully for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Error processing user created event", e);
        }
    }
}

