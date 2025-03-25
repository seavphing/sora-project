package com.soramitsu.assignment.gift_code_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedeemGiftCodeRequest {
    @NotNull
    private Long userId;

    @NotBlank
    private String walletId;
}