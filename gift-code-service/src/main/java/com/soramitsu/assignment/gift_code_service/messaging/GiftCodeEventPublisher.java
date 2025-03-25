package com.soramitsu.assignment.gift_code_service.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soramitsu.assignment.gift_code_service.model.GiftCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GiftCodeEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.exchange.gift-codes}")
    private String giftCodeExchange;

    @Value("${rabbitmq.routing-key.gift-code-created}")
    private String giftCodeCreatedRoutingKey;

    @Value("${rabbitmq.routing-key.gift-code-redeemed}")
    private String giftCodeRedeemedRoutingKey;

    public void publishGiftCodeCreatedEvent(GiftCode giftCode) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("code", giftCode.getCode());
            event.put("amount", giftCode.getAmount());
            event.put("currencyCode", giftCode.getCurrencyCode());
            event.put("createdByUserId", giftCode.getCreatedByUserId());
            event.put("expiresAt", giftCode.getExpiresAt());
            event.put("createdAt", giftCode.getCreatedAt());

            String message = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(giftCodeExchange, giftCodeCreatedRoutingKey, message);

            log.info("Published gift code created event: {}", giftCode.getCode());
        } catch (Exception e) {
            log.error("Failed to publish gift code created event", e);
        }
    }

    public void publishGiftCodeRedeemedEvent(GiftCode giftCode, String walletId) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("code", giftCode.getCode());
            event.put("amount", giftCode.getAmount());
            event.put("currencyCode", giftCode.getCurrencyCode());
            event.put("createdByUserId", giftCode.getCreatedByUserId());
            event.put("redeemedByUserId", giftCode.getRedeemedByUserId());
            event.put("walletId", walletId);
            event.put("redeemedAt", giftCode.getRedeemedAt());

            String message = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(giftCodeExchange, giftCodeRedeemedRoutingKey, message);

            log.info("Published gift code redeemed event: {}", giftCode.getCode());
        } catch (Exception e) {
            log.error("Failed to publish gift code redeemed event", e);
        }
    }
}
