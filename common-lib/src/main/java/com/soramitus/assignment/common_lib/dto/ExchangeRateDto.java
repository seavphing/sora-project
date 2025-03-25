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
public class ExchangeRateDto {
    private String sourceCurrency;
    private String targetCurrency;
    private BigDecimal rate;
    private LocalDateTime updatedAt;
}
