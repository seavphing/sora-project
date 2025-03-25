package com.soramitsu.assignment.wallet_service.controller;

import com.soramitsu.assignment.wallet_service.dto.CreateWalletRequest;
import com.soramitsu.assignment.wallet_service.dto.GiftCodeRedemptionRequest;
import com.soramitsu.assignment.wallet_service.dto.TransferRequest;
import com.soramitsu.assignment.wallet_service.dto.WalletResponse;
import com.soramitsu.assignment.wallet_service.service.GiftCodeService;
import com.soramitsu.assignment.wallet_service.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final WalletService walletService;
    private final GiftCodeService giftCodeService;

    /**
     * Create a new wallet
     *
     * @param request Wallet creation details
     * @return Created wallet
     */
    @PostMapping("/create")
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        log.info("Received request to create wallet for user ID: {}", request.getUserId());
        WalletResponse wallet = walletService.createWallet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
    }

    /**
     * Get all wallets for a user
     *
     * @param userId User ID
     * @return List of wallets
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WalletResponse>> getWalletsByUserId(@PathVariable Long userId) {
        log.info("Received request to get wallets for user ID: {}", userId);
        List<WalletResponse> wallets = walletService.getWalletsByUserId(userId);
        return ResponseEntity.ok(wallets);
    }

    /**
     * Get wallet by ID
     *
     * @param walletId Wallet ID
     * @return Wallet if found
     */
    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> getWalletById(@PathVariable UUID walletId) {
        log.info("Received request to get wallet by ID: {}", walletId);
        WalletResponse wallet = walletService.getWalletById(walletId);
        return ResponseEntity.ok(wallet);
    }

    /**
     * Get wallet by user ID and currency code
     *
     * @param userId User ID
     * @param currencyCode Currency code
     * @return Wallet if found
     */
    @GetMapping("/user/{userId}/currency/{currencyCode}")
    public ResponseEntity<WalletResponse> getWalletByUserIdAndCurrencyCode(
            @PathVariable Long userId,
            @PathVariable String currencyCode) {
        log.info("Received request to get wallet for user ID: {} with currency: {}", userId, currencyCode);
        WalletResponse wallet = walletService.getWalletByUserIdAndCurrencyCode(userId, currencyCode);
        return ResponseEntity.ok(wallet);
    }

    /**
     * Transfer funds between wallets
     *
     * @param request Transfer details
     * @return Transfer status
     */
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transferFunds(@Valid @RequestBody TransferRequest request) {
        log.info("Received request to transfer funds: {}", request);

        // Process the transfer asynchronously
        CompletableFuture.runAsync(() -> walletService.transferFunds(request));

        return ResponseEntity.accepted().body(Map.of(
                "message", "Transfer request accepted and is being processed",
                "sourceWalletId", request.getSourceWalletId(),
                "targetWalletId", request.getTargetWalletId(),
                "amount", request.getAmount()
        ));
    }

    /**
     * Redeem a gift code
     *
     * @param request Gift code redemption details
     * @return Redemption status
     */
    @PostMapping("/redeem-gift-code")
    public ResponseEntity<Map<String, Object>> redeemGiftCode(@Valid @RequestBody GiftCodeRedemptionRequest request) {
        log.info("Received request to redeem gift code for wallet ID: {}", request.getWalletId());

        // Process the gift code redemption asynchronously
        CompletableFuture.runAsync(() -> giftCodeService.redeemGiftCode(request));

        return ResponseEntity.accepted().body(Map.of(
                "message", "Gift code redemption request is being processed",
                "walletId", request.getWalletId(),
                "giftCode", request.getGiftCode()
        ));
    }

    /**
     * Health check endpoint
     *
     * @return Service status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
