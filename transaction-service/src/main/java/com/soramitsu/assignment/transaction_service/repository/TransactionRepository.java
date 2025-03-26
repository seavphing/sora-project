package com.soramitsu.assignment.transaction_service.repository;

import com.soramitsu.assignment.transaction_service.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    Page<Transaction> findBySenderIdOrReceiverId(Long senderId, Long receiverId, Pageable pageable);

    List<Transaction> findBySenderIdOrReceiverIdAndCreatedAtBetween(
            Long senderId,
            Long receiverId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<Transaction> findBySenderWalletIdOrReceiverWalletId(
            UUID senderWalletId,
            UUID receiverWalletId
    );
}
