package com.soramitsu.assignment.gift_code_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiftCodeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;
    private BigDecimal amount;
    private String currencyCode;
    private boolean redeemed;
    private Long createdByUserId;
    private Long redeemedByUserId;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime redeemedAt;
}
