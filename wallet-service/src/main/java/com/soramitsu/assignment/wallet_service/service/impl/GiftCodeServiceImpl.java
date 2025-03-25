package com.soramitsu.assignment.wallet_service.service.impl;

import com.soramitsu.assignment.wallet_service.dto.GiftCodeRedemptionRequest;
import com.soramitsu.assignment.wallet_service.exception.InvalidGiftCodeException;
import com.soramitsu.assignment.wallet_service.exception.ResourceNotFoundException;
import com.soramitsu.assignment.wallet_service.service.GiftCodeService;
import com.soramitsu.assignment.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
public class GiftCodeServiceImpl implements GiftCodeService {

    private final WalletService walletService;
    private final RestTemplate restTemplate;
    private final String giftCodeServiceUrl;

    public GiftCodeServiceImpl(WalletService walletService,
                               RestTemplate restTemplate,
                               @Value("${gift-code-service.url:http://gift-code-service}") String giftCodeServiceUrl) {
        this.walletService = walletService;
        this.restTemplate = restTemplate;
        this.giftCodeServiceUrl = giftCodeServiceUrl;
    }

    @Override
    public void redeemGiftCode(GiftCodeRedemptionRequest request) {
        log.info("Redeeming gift code for wallet ID: {}", request.getWalletId());

        try {
            // Verify wallet exists
            walletService.getWalletById(request.getWalletId());

            // Call gift code service to validate and redeem the code
            String url = giftCodeServiceUrl + "/redeem/" + request.getGiftCode();

            // In a real implementation, we would call the gift code service
            // Map<String, Object> response = restTemplate.postForObject(
            //     url,
            //     Map.of("walletId", request.getWalletId()),
            //     Map.class
            // );

            // For demo, we'll simulate a successful response
            Map<String, Object> giftCodeDetails = simulateGiftCodeResponse(request.getGiftCode());

            if (giftCodeDetails == null || (boolean) giftCodeDetails.get("redeemed")) {
                log.error("Gift code {} is invalid or already redeemed", request.getGiftCode());
                throw new InvalidGiftCodeException("Gift code is invalid or already redeemed");
            }

            BigDecimal amount = new BigDecimal(giftCodeDetails.get("amount").toString());
            String currencyCode = (String) giftCodeDetails.get("currencyCode");

            // Add funds to wallet
            walletService.addFunds(
                    request.getWalletId(),
                    amount,
                    currencyCode,
                    "GIFT_CODE",
                    request.getGiftCode()
            );

            log.info("Gift code {} successfully redeemed for wallet ID: {}",
                    request.getGiftCode(), request.getWalletId());

        } catch (ResourceNotFoundException | InvalidGiftCodeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to process gift code redemption", e);
            throw new RuntimeException("Failed to process gift code redemption", e);
        }
    }

    /**
     * Simulate response from gift code service
     * In a real implementation, this would be a call to the gift code service
     *
     * @param giftCode Gift code
     * @return Simulated response
     */
    private Map<String, Object> simulateGiftCodeResponse(String giftCode) {
        // For demo purposes only
        if ("INVALID".equals(giftCode)) {
            return null;
        } else if ("USED".equals(giftCode)) {
            return Map.of(
                    "code", giftCode,
                    "amount", "50.00",
                    "currencyCode", "USD",
                    "redeemed", true
            );
        } else {
            return Map.of(
                    "code", giftCode,
                    "amount", "50.00",
                    "currencyCode", "USD",
                    "redeemed", false
            );
        }
    }
}
