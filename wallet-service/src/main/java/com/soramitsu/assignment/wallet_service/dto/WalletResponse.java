package com.soramitsu.assignment.wallet_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
    private UUID id;
    private Long userId;
    private BigDecimal balance;
    private String currencyCode;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
