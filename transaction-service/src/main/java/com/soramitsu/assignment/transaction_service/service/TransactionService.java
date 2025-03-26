package com.soramitsu.assignment.transaction_service.service;

import com.soramitsu.assignment.transaction_service.dto.TransactionDto;
import com.soramitsu.assignment.transaction_service.dto.TransactionFilterRequest;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionService {

    public TransactionDto getTransactionById(String id);

    public Page<TransactionDto> getUserTransactions(TransactionFilterRequest filterRequest);

    public List<TransactionDto> getUserTransactionsByDateRange(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate);

    public List<TransactionDto> getWalletTransactions(UUID walletId);

    public void saveTransaction(TransactionDto transactionDto);

};
