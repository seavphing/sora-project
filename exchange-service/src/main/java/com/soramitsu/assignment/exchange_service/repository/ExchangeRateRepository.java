package com.soramitsu.assignment.exchange_service.repository;

import com.soramitsu.assignment.exchange_service.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findBySourceCurrencyAndTargetCurrency(String sourceCurrency, String targetCurrency);

    boolean existsBySourceCurrencyAndTargetCurrency(String sourceCurrency, String targetCurrency);
}
