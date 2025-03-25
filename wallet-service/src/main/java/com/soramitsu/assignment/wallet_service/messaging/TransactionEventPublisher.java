package com.soramitsu.assignment.wallet_service.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.exchange.transactions}")
    private String transactionExchange;

    @Value("${rabbitmq.routing-key.transaction-created}")
    private String transactionCreatedRoutingKey;

    /**
     * Publish a transfer transaction event
     *
     * @param transactionId Unique transaction ID
     * @param senderId Sender user ID
     * @param receiverId Receiver user ID
     * @param senderWalletId Sender wallet ID
     * @param receiverWalletId Receiver wallet ID
     * @param sourceAmount Source amount
     * @param targetAmount Target amount (may differ due to currency conversion)
     * @param sourceCurrency Source currency code
     * @param targetCurrency Target currency code
     * @param description Transaction description
     */
    public void publishTransferEvent(
            String transactionId,
            Long senderId,
            Long receiverId,
            String senderWalletId,
            String receiverWalletId,
            BigDecimal sourceAmount,
            BigDecimal targetAmount,
            String sourceCurrency,
            String targetCurrency,
            String description
    ) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("id", transactionId);
            event.put("type", "TRANSFER");
            event.put("status", "COMPLETED");
            event.put("senderId", senderId);
            event.put("receiverId", receiverId);
            event.put("senderWalletId", senderWalletId);
            event.put("receiverWalletId", receiverWalletId);
            event.put("sourceAmount", sourceAmount);
            event.put("targetAmount", targetAmount);
            event.put("sourceCurrency", sourceCurrency);
            event.put("targetCurrency", targetCurrency);
            event.put("description", description);
            event.put("createdAt", LocalDateTime.now().toString());

            String message = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(transactionExchange, transactionCreatedRoutingKey, message);

            log.info("Published transfer transaction event: {}", transactionId);
        } catch (Exception e) {
            log.error("Failed to publish transfer transaction event", e);
        }
    }

    /**
     * Publish an event when funds are added to a wallet
     *
     * @param transactionId Unique transaction ID
     * @param userId User ID
     * @param walletId Wallet ID
     * @param sourceAmount Source amount
     * @param targetAmount Target amount (may differ due to currency conversion)
     * @param sourceCurrency Source currency code
     * @param targetCurrency Target currency code
     * @param source Source of funds (e.g., "GIFT_CODE", "DEPOSIT")
     * @param reference Reference information (e.g., gift code)
     */
    public void publishFundAddedEvent(
            String transactionId,
            Long userId,
            String walletId,
            BigDecimal sourceAmount,
            BigDecimal targetAmount,
            String sourceCurrency,
            String targetCurrency,
            String source,
            String reference
    ) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("id", transactionId);
            event.put("type", source);
            event.put("status", "COMPLETED");
            event.put("receiverId", userId);
            event.put("receiverWalletId", walletId);
            event.put("sourceAmount", sourceAmount);
            event.put("targetAmount", targetAmount);
            event.put("sourceCurrency", sourceCurrency);
            event.put("targetCurrency", targetCurrency);
            event.put("reference", reference);
            event.put("createdAt", LocalDateTime.now().toString());

            String message = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(transactionExchange, transactionCreatedRoutingKey, message);

            log.info("Published fund added event: {}", transactionId);
        } catch (Exception e) {
            log.error("Failed to publish fund added event", e);
        }
    }
}
