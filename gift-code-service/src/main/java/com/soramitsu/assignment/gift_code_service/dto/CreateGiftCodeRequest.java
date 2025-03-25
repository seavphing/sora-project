package com.soramitsu.assignment.gift_code_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGiftCodeRequest {
    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private String currencyCode;

    @NotNull
    private Long userId;

    private Integer validityDays;
}
