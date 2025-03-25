package com.soramitus.assignment.common_lib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletDto {

    private String id;
    private Long userId;
    private BigDecimal balance;
    private String currencyCode;
    private boolean active;
}
