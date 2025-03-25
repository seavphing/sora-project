package com.soramitsu.assignment.gift_code_service.exception;

public class GiftCodeExpiredException extends RuntimeException {
    public GiftCodeExpiredException(String message) {
        super(message);
    }
}
