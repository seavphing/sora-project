package com.soramitsu.assignment.exchange_service.service;

import com.soramitsu.assignment.exchange_service.dto.ExchangeRateDto;
import com.soramitsu.assignment.exchange_service.dto.ExchangeRateRequest;
import com.soramitsu.assignment.exchange_service.model.ExchangeRate;

import java.math.BigDecimal;
import java.util.List;

public interface ExchangeRateService {

    public ExchangeRateDto getExchangeRate(String sourceCurrency, String targetCurrency);

    public List<ExchangeRateDto> getAllExchangeRates();

    public ExchangeRateDto createOrUpdateExchangeRate(ExchangeRateRequest request);

    public void seedDefaultExchangeRates();

    public BigDecimal convertAmount(String sourceCurrency, String targetCurrency, BigDecimal amount);


}
