package com.soramitsu.assignment.wallet_service.repository;

import com.soramitsu.assignment.wallet_service.model.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    List<Wallet> findByUserId(Long userId);

    List<Wallet> findByUserIdAndActiveTrue(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findById(UUID id);

    Optional<Wallet> findByUserIdAndCurrencyCode(Long userId, String currencyCode);

    boolean existsByUserIdAndCurrencyCodeAndActiveTrue(Long userId, String currencyCode);

    @Query("SELECT COUNT(w) FROM Wallet w WHERE w.userId = :userId AND w.active = true")
    long countActiveWalletsByUserId(@Param("userId") Long userId);
}
