package com.soramitsu.assignment.wallet_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiftCodeRedemptionRequest {

    @NotNull(message = "Wallet ID is required")
    private UUID walletId;

    @NotBlank(message = "Gift code is required")
    private String giftCode;
}
