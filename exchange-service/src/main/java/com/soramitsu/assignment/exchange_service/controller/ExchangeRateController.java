package com.soramitsu.assignment.exchange_service.controller;

import com.soramitsu.assignment.exchange_service.dto.ExchangeRateDto;
import com.soramitsu.assignment.exchange_service.dto.ExchangeRateRequest;
import com.soramitsu.assignment.exchange_service.service.ExchangeRateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exchange-rates")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping
    public ResponseEntity<List<ExchangeRateDto>> getAllExchangeRates() {
        List<ExchangeRateDto> exchangeRates = exchangeRateService.getAllExchangeRates();
        return ResponseEntity.ok(exchangeRates);
    }

    @GetMapping("/rate")
    public ResponseEntity<ExchangeRateDto> getExchangeRate(
            @RequestParam String source,
            @RequestParam String target) {

        ExchangeRateDto exchangeRate = exchangeRateService.getExchangeRate(source, target);
        return ResponseEntity.ok(exchangeRate);
    }

    @GetMapping("/convert")
    public ResponseEntity<Map<String, Object>> convertAmount(
            @RequestParam String source,
            @RequestParam String target,
            @RequestParam BigDecimal amount) {

        ExchangeRateDto exchangeRate = exchangeRateService.getExchangeRate(source, target);
        BigDecimal convertedAmount = exchangeRateService.convertAmount(source, target, amount);

        Map<String, Object> response = Map.of(
                "sourceCurrency", source,
                "targetCurrency", target,
                "sourceAmount", amount,
                "targetAmount", convertedAmount,
                "rate", exchangeRate.getRate(),
                "updatedAt", exchangeRate.getUpdatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ExchangeRateDto> createOrUpdateExchangeRate(
            @Valid @RequestBody ExchangeRateRequest request) {

        ExchangeRateDto exchangeRate = exchangeRateService.createOrUpdateExchangeRate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(exchangeRate);
    }
}
