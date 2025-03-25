package com.soramitsu.assignment.wallet_service.service.impl;

import com.soramitsu.assignment.wallet_service.dto.CreateWalletRequest;
import com.soramitsu.assignment.wallet_service.dto.TransferRequest;
import com.soramitsu.assignment.wallet_service.dto.WalletResponse;
import com.soramitsu.assignment.wallet_service.exception.InsufficientBalanceException;
import com.soramitsu.assignment.wallet_service.exception.ResourceNotFoundException;
import com.soramitsu.assignment.wallet_service.messaging.TransactionEventPublisher;
import com.soramitsu.assignment.wallet_service.model.Wallet;
import com.soramitsu.assignment.wallet_service.repository.WalletRepository;
import com.soramitsu.assignment.wallet_service.service.ExchangeRateService;
import com.soramitsu.assignment.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final ExchangeRateService exchangeRateService;
    private final TransactionEventPublisher transactionEventPublisher;

    @Override
    @Transactional(readOnly = false)
    public WalletResponse createWallet(CreateWalletRequest request) {
        log.info("Creating wallet for user ID: {} with currency: {}", request.getUserId(), request.getCurrencyCode());

        // Check if user already has a wallet with this currency
        if (hasActiveWallet(request.getUserId(), request.getCurrencyCode())) {
            log.warn("User ID: {} already has an active wallet with currency: {}",
                    request.getUserId(), request.getCurrencyCode());
            throw new IllegalArgumentException(
                    "User already has an active wallet with currency: " + request.getCurrencyCode());
        }

        Wallet wallet = Wallet.builder()
                .userId(request.getUserId())
                .balance(BigDecimal.ZERO)
                .currencyCode(request.getCurrencyCode())
                .active(true)
                .build();

        Wallet savedWallet = walletRepository.save(wallet);
        log.info("Wallet created with ID: {} for user ID: {}", savedWallet.getId(), request.getUserId());

        return mapToWalletResponse(savedWallet);
    }

    @Override
    @Transactional
    @Cacheable(value = "walletsByUser", key = "#userId")
    public List<WalletResponse> getWalletsByUserId(Long userId) {
        log.info("Getting wallets for user ID: {}", userId);
        List<Wallet> wallets = walletRepository.findByUserIdAndActiveTrue(userId);

        return wallets.stream()
                .map(this::mapToWalletResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @Cacheable(value = "wallets", key = "#id")
    public WalletResponse getWalletById(UUID id) {
        log.info("Getting wallet by ID: {}", id);
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Wallet not found with ID: {}", id);
                    return new ResourceNotFoundException("Wallet not found with ID: " + id);
                });

        return mapToWalletResponse(wallet);
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(value = {"wallets", "walletsByUser"}, allEntries = true)
    public void transferFunds(TransferRequest request) {
        log.info("Transferring {} from wallet ID: {} to wallet ID: {}",
                request.getAmount(), request.getSourceWalletId(), request.getTargetWalletId());

        Wallet sourceWallet = walletRepository.findById(request.getSourceWalletId())
                .orElseThrow(() -> {
                    log.error("Source wallet not found with ID: {}", request.getSourceWalletId());
                    return new ResourceNotFoundException("Source wallet not found");
                });

        Wallet targetWallet = walletRepository.findById(request.getTargetWalletId())
                .orElseThrow(() -> {
                    log.error("Target wallet not found with ID: {}", request.getTargetWalletId());
                    return new ResourceNotFoundException("Target wallet not found");
                });

        // Check if source wallet has sufficient balance
        if (sourceWallet.getBalance().compareTo(request.getAmount()) < 0) {
            log.error("Insufficient balance in wallet ID: {}", request.getSourceWalletId());
            throw new InsufficientBalanceException("Insufficient balance for transfer");
        }

        // Calculate exchange rate if currencies are different
        BigDecimal exchangeAmount = request.getAmount();
        if (!sourceWallet.getCurrencyCode().equals(targetWallet.getCurrencyCode())) {
            exchangeAmount = exchangeRateService.convertAmount(
                    request.getAmount(),
                    sourceWallet.getCurrencyCode(),
                    targetWallet.getCurrencyCode()
            );
            log.info("Converted {} {} to {} {}",
                    request.getAmount(), sourceWallet.getCurrencyCode(),
                    exchangeAmount, targetWallet.getCurrencyCode());
        }

        // Update balances
        sourceWallet.setBalance(sourceWallet.getBalance().subtract(request.getAmount()));
        targetWallet.setBalance(targetWallet.getBalance().add(exchangeAmount));

        // Save updated wallets
        walletRepository.save(sourceWallet);
        walletRepository.save(targetWallet);

        // Publish transaction event
        transactionEventPublisher.publishTransferEvent(
                UUID.randomUUID().toString(),
                sourceWallet.getUserId(),
                targetWallet.getUserId(),
                sourceWallet.getId().toString(),
                targetWallet.getId().toString(),
                request.getAmount(),
                exchangeAmount,
                sourceWallet.getCurrencyCode(),
                targetWallet.getCurrencyCode(),
                request.getDescription()
        );

        log.info("Transfer completed successfully from wallet ID: {} to wallet ID: {}",
                request.getSourceWalletId(), request.getTargetWalletId());
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(value = {"wallets", "walletsByUser"}, allEntries = true)
    public void addFunds(UUID walletId, BigDecimal amount, String currencyCode, String source, String reference) {
        log.info("Adding {} {} to wallet ID: {} from source: {}", amount, currencyCode, walletId, source);

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    log.error("Wallet not found with ID: {}", walletId);
                    return new ResourceNotFoundException("Wallet not found");
                });

        // Calculate exchange rate if currencies are different
        BigDecimal exchangeAmount = amount;
        if (!currencyCode.equals(wallet.getCurrencyCode())) {
            exchangeAmount = exchangeRateService.convertAmount(
                    amount,
                    currencyCode,
                    wallet.getCurrencyCode()
            );
            log.info("Converted {} {} to {} {}",
                    amount, currencyCode, exchangeAmount, wallet.getCurrencyCode());
        }

        // Update balance
        wallet.setBalance(wallet.getBalance().add(exchangeAmount));

        // Save updated wallet
        walletRepository.save(wallet);

        // Publish transaction event
        transactionEventPublisher.publishFundAddedEvent(
                UUID.randomUUID().toString(),
                wallet.getUserId(),
                walletId.toString(),
                amount,
                exchangeAmount,
                currencyCode,
                wallet.getCurrencyCode(),
                source,
                reference
        );

        log.info("Funds added successfully to wallet ID: {}", walletId);
    }

    @Override
    public WalletResponse getWalletByUserIdAndCurrencyCode(Long userId, String currencyCode) {
        log.info("Getting wallet for user ID: {} with currency code: {}", userId, currencyCode);

        Wallet wallet = walletRepository.findByUserIdAndCurrencyCode(userId, currencyCode)
                .orElseThrow(() -> {
                    log.error("Wallet not found for user ID: {} with currency code: {}", userId, currencyCode);
                    return new ResourceNotFoundException("Wallet not found with currency code: " + currencyCode);
                });

        return mapToWalletResponse(wallet);
    }

    @Override
    public boolean hasActiveWallet(Long userId, String currencyCode) {
        return walletRepository.existsByUserIdAndCurrencyCodeAndActiveTrue(userId, currencyCode);
    }

    /**
     * Map Wallet entity to WalletResponse DTO
     *
     * @param wallet Wallet entity
     * @return WalletResponse DTO
     */
    private WalletResponse mapToWalletResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .currencyCode(wallet.getCurrencyCode())
                .active(wallet.isActive())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }
}
