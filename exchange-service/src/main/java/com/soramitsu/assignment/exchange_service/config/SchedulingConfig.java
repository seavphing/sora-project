package com.soramitsu.assignment.exchange_service.config;

import com.soramitsu.assignment.exchange_service.service.ExchangeRateService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SchedulingConfig {

    private final ExchangeRateService exchangeRateService;

    @PostConstruct
    public void initDefaultRates() {
        log.info("Initializing default exchange rates");
        exchangeRateService.seedDefaultExchangeRates();
    }

    @Scheduled(cron = "0 0 */6 * * *") // Every 6 hours
    public void updateExchangeRates() {
        log.info("Scheduled exchange rate update - would fetch from external API in production");
        // I prepare this for future implementation if really needed to call third party API
    }
}
