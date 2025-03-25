package com.soramitsu.assignment.wallet_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiftCodeResponse {
    private String code;
    private BigDecimal amount;
    private String currencyCode;
    private boolean redeemed;
}
