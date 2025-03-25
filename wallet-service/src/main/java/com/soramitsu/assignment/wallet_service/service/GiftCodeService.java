package com.soramitsu.assignment.wallet_service.service;

import com.soramitsu.assignment.wallet_service.dto.GiftCodeRedemptionRequest;

public interface GiftCodeService {

    void redeemGiftCode(GiftCodeRedemptionRequest request);
}
