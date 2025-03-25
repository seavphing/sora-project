package com.soramitsu.assignment.wallet_service.service.impl;

import com.soramitsu.assignment.wallet_service.service.ExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final RestTemplate restTemplate;
    private final String exchangeRateServiceUrl;

    // Fallback exchange rates (would come from a real service in production)
    private static final Map<String, BigDecimal> FALLBACK_RATES = new ConcurrentHashMap<>();

    static {
        // Some sample exchange rates relative to USD
        FALLBACK_RATES.put("USD_EUR", new BigDecimal("0.92"));
        FALLBACK_RATES.put("USD_GBP", new BigDecimal("0.79"));
        FALLBACK_RATES.put("USD_JPY", new BigDecimal("151.50"));
        FALLBACK_RATES.put("USD_KHR", new BigDecimal("4100.00"));
        FALLBACK_RATES.put("EUR_USD", new BigDecimal("1.09"));
        FALLBACK_RATES.put("GBP_USD", new BigDecimal("1.27"));
        FALLBACK_RATES.put("JPY_USD", new BigDecimal("0.0066"));
        FALLBACK_RATES.put("KHR_USD", new BigDecimal("0.00024"));
    }

    public ExchangeRateServiceImpl(RestTemplate restTemplate,
                                   @Value("${exchange-rate-service.url:http://exchange-rate-service}") String exchangeRateServiceUrl) {
        this.restTemplate = restTemplate;
        this.exchangeRateServiceUrl = exchangeRateServiceUrl;
    }

    @Override
    @Cacheable(value = "exchangeRates", key = "#sourceCurrency + '_' + #targetCurrency")
    public BigDecimal getExchangeRate(String sourceCurrency, String targetCurrency) {
        log.info("Getting exchange rate from {} to {}", sourceCurrency, targetCurrency);

        // If source and target are the same, return 1.0 exchange rate
        if (sourceCurrency.equalsIgnoreCase(targetCurrency)) {
            return BigDecimal.ONE;
        }

        try {
            // Try to get from exchange rate service
            String url = exchangeRateServiceUrl + "/rate?source=" + sourceCurrency + "&target=" + targetCurrency;
            // In a real implementation, we would call the exchange rate service
            // ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);
            // return response.getRate();

            // For demo, we'll use fallback rates
            return getFallbackRate(sourceCurrency, targetCurrency);
        } catch (Exception e) {
            log.warn("Error getting exchange rate from service, using fallback rates", e);
            // Fallback to local rates if service is unavailable
            return getFallbackRate(sourceCurrency, targetCurrency);
        }
    }

    @Override
    public BigDecimal convertAmount(BigDecimal amount, String sourceCurrency, String targetCurrency) {
        log.info("Converting {} {} to {}", amount, sourceCurrency, targetCurrency);

        // If source and target are the same, no conversion needed
        if (sourceCurrency.equalsIgnoreCase(targetCurrency)) {
            return amount;
        }

        BigDecimal exchangeRate = getExchangeRate(sourceCurrency, targetCurrency);
        return amount.multiply(exchangeRate).setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * Get fallback exchange rate for a currency pair
     *
     * @param sourceCurrency Source currency code
     * @param targetCurrency Target currency code
     * @return Exchange rate
     */
    private BigDecimal getFallbackRate(String sourceCurrency, String targetCurrency) {
        String key = sourceCurrency + "_" + targetCurrency;

        if (FALLBACK_RATES.containsKey(key)) {
            return FALLBACK_RATES.get(key);
        }

        // If direct conversion not found, try to go through USD
        if (!sourceCurrency.equals("USD") && !targetCurrency.equals("USD")) {
            BigDecimal sourceToUsd = getFallbackRate(sourceCurrency, "USD");
            BigDecimal usdToTarget = getFallbackRate("USD", targetCurrency);
            return sourceToUsd.multiply(usdToTarget).setScale(6, RoundingMode.HALF_UP);
        }

        // Default to 1:1 if no conversion found
        log.warn("No conversion rate found for {} to {}, using 1:1", sourceCurrency, targetCurrency);
        return BigDecimal.ONE;
    }
}
