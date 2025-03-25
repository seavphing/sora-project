package com.soramitsu.assignment.wallet_service.service;

import java.math.BigDecimal;

public interface ExchangeRateService {

    BigDecimal getExchangeRate(String sourceCurrency, String targetCurrency);

    BigDecimal convertAmount(BigDecimal amount, String sourceCurrency, String targetCurrency);
}
