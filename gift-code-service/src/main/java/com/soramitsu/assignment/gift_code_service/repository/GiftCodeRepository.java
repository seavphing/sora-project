package com.soramitsu.assignment.gift_code_service.repository;

import com.soramitsu.assignment.gift_code_service.model.GiftCode;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GiftCodeRepository extends JpaRepository<GiftCode, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<GiftCode> findByCode(String code);

    List<GiftCode> findByCreatedByUserId(Long userId);

    List<GiftCode> findByRedeemedByUserId(Long userId);

    List<GiftCode> findByCreatedByUserIdAndExpiresAtAfterAndRedeemedFalse(Long userId, LocalDateTime now);
}
