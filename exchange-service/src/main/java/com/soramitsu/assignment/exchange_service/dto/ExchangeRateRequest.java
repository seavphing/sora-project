package com.soramitsu.assignment.exchange_service.dto;

import jakarta.validation.constraints.NotBlank;
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
public class ExchangeRateRequest {
    @NotBlank
    private String sourceCurrency;

    @NotBlank
    private String targetCurrency;

    @NotNull
    @Positive
    private BigDecimal rate;
}
