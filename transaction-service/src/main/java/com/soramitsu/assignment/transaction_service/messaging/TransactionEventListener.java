package com.soramitsu.assignment.transaction_service.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soramitsu.assignment.transaction_service.dto.TransactionDto;
import com.soramitsu.assignment.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventListener {

    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${rabbitmq.queue.transactions}")
    public void handleTransactionEvent(String message) {
        try {
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            log.info("Received transaction event: {}", event.get("id"));

            // Map event to TransactionDto
            TransactionDto transactionDto = mapEventToDto(event);

            // Save transaction
            transactionService.saveTransaction(transactionDto);

            log.info("Transaction saved: {}", transactionDto.getId());
        } catch (Exception e) {
            log.error("Error processing transaction event", e);
        }
    }

    @SuppressWarnings("unchecked")
    private TransactionDto mapEventToDto(Map<String, Object> event) {
        return TransactionDto.builder()
                .id((String) event.get("id"))
                .type((String) event.get("type"))
                .status((String) event.get("status"))
                .senderId(event.get("senderId") != null ? Long.valueOf(event.get("senderId").toString()) : null)
                .receiverId(event.get("receiverId") != null ? Long.valueOf(event.get("receiverId").toString()) : null)
                .senderWalletId(event.get("senderWalletId") != null ? UUID.fromString((String)event.get("senderWalletId")) : null)
                .receiverWalletId(event.get("receiverWalletId") != null ? UUID.fromString((String)event.get("receiverWalletId")) : null)
                .amount(new BigDecimal(event.get("sourceAmount").toString()))
                .currencyCode((String) event.get("sourceCurrency"))
                .description((String) event.get("description"))
                .createdAt(LocalDateTime.parse(event.get("createdAt").toString()))
                .build();
    }
}
