package com.soramitsu.assignment.wallet_service.service;

import com.soramitsu.assignment.wallet_service.dto.CreateWalletRequest;
import com.soramitsu.assignment.wallet_service.dto.TransferRequest;
import com.soramitsu.assignment.wallet_service.dto.WalletResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface WalletService {

    WalletResponse createWallet(CreateWalletRequest request);

    List<WalletResponse> getWalletsByUserId(Long userId);

    WalletResponse getWalletById(UUID id);

    void transferFunds(TransferRequest request);

    void addFunds(UUID walletId, BigDecimal amount, String currencyCode, String source, String reference);

    WalletResponse getWalletByUserIdAndCurrencyCode(Long userId, String currencyCode);

    boolean hasActiveWallet(Long userId, String currencyCode);
}
