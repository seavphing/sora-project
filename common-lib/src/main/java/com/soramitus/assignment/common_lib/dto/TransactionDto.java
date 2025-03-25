package com.soramitus.assignment.common_lib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private String id;
    private Long senderId;
    private Long receiverId;
    private String senderWalletId;
    private String receiverWalletId;
    private BigDecimal amount;
    private String currencyCode;
    private String status;
    private String type; // TRANSFER, GIFT_CODE_REDEMPTION
    private LocalDateTime createdAt;
}
