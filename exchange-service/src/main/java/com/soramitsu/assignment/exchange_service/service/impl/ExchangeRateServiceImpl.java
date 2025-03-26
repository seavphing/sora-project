package com.soramitsu.assignment.exchange_service.service.impl;

import com.soramitsu.assignment.exchange_service.dto.ExchangeRateDto;
import com.soramitsu.assignment.exchange_service.dto.ExchangeRateRequest;
import com.soramitsu.assignment.exchange_service.exception.ResourceNotFoundException;
import com.soramitsu.assignment.exchange_service.model.ExchangeRate;
import com.soramitsu.assignment.exchange_service.repository.ExchangeRateRepository;
import com.soramitsu.assignment.exchange_service.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    @Override
    @Cacheable(value = "exchangeRates", key = "#sourceCurrency + '_' + #targetCurrency")
    public ExchangeRateDto getExchangeRate(String sourceCurrency, String targetCurrency) {
        // If source and target are the same, return 1.0 exchange rate
        if (sourceCurrency.equalsIgnoreCase(targetCurrency)) {
            return ExchangeRateDto.builder()
                    .sourceCurrency(sourceCurrency)
                    .targetCurrency(targetCurrency)
                    .rate(BigDecimal.ONE)
                    .build();
        }

        ExchangeRate exchangeRate = exchangeRateRepository.findBySourceCurrencyAndTargetCurrency(sourceCurrency, targetCurrency)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange rate not found for " + sourceCurrency + " to " + targetCurrency));

        return mapToDto(exchangeRate);
    }

    @Override
    public List<ExchangeRateDto> getAllExchangeRates() {
        List<ExchangeRate> exchangeRates = exchangeRateRepository.findAll();

        return exchangeRates.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "exchangeRates", key = "#request.sourceCurrency + '_' + #request.targetCurrency")
    public ExchangeRateDto createOrUpdateExchangeRate(ExchangeRateRequest request) {
        ExchangeRate exchangeRate;

        if (exchangeRateRepository.existsBySourceCurrencyAndTargetCurrency(request.getSourceCurrency(), request.getTargetCurrency())) {
            exchangeRate = exchangeRateRepository.findBySourceCurrencyAndTargetCurrency(request.getSourceCurrency(), request.getTargetCurrency())
                    .orElseThrow();
            exchangeRate.setRate(request.getRate());
        } else {
            exchangeRate = ExchangeRate.builder()
                    .sourceCurrency(request.getSourceCurrency())
                    .targetCurrency(request.getTargetCurrency())
                    .rate(request.getRate())
                    .build();
        }

        ExchangeRate savedExchangeRate = exchangeRateRepository.save(exchangeRate);

        // Create inverse rate as well if it doesn't exist
        if (!exchangeRateRepository.existsBySourceCurrencyAndTargetCurrency(request.getTargetCurrency(), request.getSourceCurrency())) {
            // Calculate inverse rate (1 / rate)
            BigDecimal inverseRate = BigDecimal.ONE.divide(request.getRate(), 8, BigDecimal.ROUND_HALF_UP);

            ExchangeRate inverseExchangeRate = ExchangeRate.builder()
                    .sourceCurrency(request.getTargetCurrency())
                    .targetCurrency(request.getSourceCurrency())
                    .rate(inverseRate)
                    .build();

            exchangeRateRepository.save(inverseExchangeRate);
        }

        return mapToDto(savedExchangeRate);
    }

    @Override
    @Transactional
    @CacheEvict(value = "exchangeRates", allEntries = true)
    public void seedDefaultExchangeRates() {
        // Only seed if no exchange rates exist
        if (exchangeRateRepository.count() == 0) {
            // Define base currencies
            String[] currencies = {"USD", "EUR", "JPY", "GBP", "AUD", "CAD", "CHF", "CNY"};

            // Define rates relative to USD
            double[] usdRates = {1.0, 0.92, 151.50, 0.79, 1.52, 1.37, 0.90, 7.20};

            // Create exchange rates for all currency pairs
            for (int i = 0; i < currencies.length; i++) {
                for (int j = 0; j < currencies.length; j++) {
                    if (i != j) {
                        // Calculate cross rate
                        double rate = usdRates[j] / usdRates[i];

                        ExchangeRate exchangeRate = ExchangeRate.builder()
                                .sourceCurrency(currencies[i])
                                .targetCurrency(currencies[j])
                                .rate(BigDecimal.valueOf(rate))
                                .build();

                        exchangeRateRepository.save(exchangeRate);
                    }
                }
            }
        }
    }
    @Override
    public BigDecimal convertAmount(String sourceCurrency, String targetCurrency, BigDecimal amount) {
        ExchangeRateDto exchangeRate = getExchangeRate(sourceCurrency, targetCurrency);
        return amount.multiply(exchangeRate.getRate());
    }

    private ExchangeRateDto mapToDto(ExchangeRate exchangeRate) {
        return ExchangeRateDto.builder()
                .sourceCurrency(exchangeRate.getSourceCurrency())
                .targetCurrency(exchangeRate.getTargetCurrency())
                .rate(exchangeRate.getRate())
                .updatedAt(exchangeRate.getUpdatedAt())
                .build();
    }
}
