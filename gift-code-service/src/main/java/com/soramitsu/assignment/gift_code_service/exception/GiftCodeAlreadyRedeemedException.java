package com.soramitsu.assignment.gift_code_service.exception;

public class GiftCodeAlreadyRedeemedException extends RuntimeException {
    public GiftCodeAlreadyRedeemedException(String message) {
        super(message);
    }
}
