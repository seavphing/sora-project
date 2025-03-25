package com.soramitus.assignment.auth_service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soramitus.assignment.auth_service.model.User;
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
public class UserEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.exchange.user}")
    private String userExchange;

    @Value("${rabbitmq.routing-key.user-created}")
    private String userCreatedRoutingKey;

    public void publishUserCreatedEvent(User user) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("userId", user.getId());
            event.put("username", user.getUsername());
            event.put("email", user.getEmail());
            event.put("defaultCurrency", user.getDefaultCurrency());
            event.put("eventType", "USER_CREATED");
            event.put("timestamp", System.currentTimeMillis());

            String message = objectMapper.writeValueAsString(event);

            rabbitTemplate.convertAndSend(userExchange, userCreatedRoutingKey, message);

            log.info("Published user created event for userId: {}", user.getId());
        } catch (JsonProcessingException e) {
            log.error("Error serializing user created event", e);
        } catch (Exception e) {
            log.error("Error publishing user created event", e);
        }
    }
}
