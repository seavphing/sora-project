package com.soramitsu.assignment.transaction_service.service.impl;

import com.soramitsu.assignment.transaction_service.dto.TransactionDto;
import com.soramitsu.assignment.transaction_service.dto.TransactionFilterRequest;
import com.soramitsu.assignment.transaction_service.exception.ResourceNotFoundException;
import com.soramitsu.assignment.transaction_service.model.Transaction;
import com.soramitsu.assignment.transaction_service.repository.TransactionRepository;
import com.soramitsu.assignment.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    @Cacheable(value = "transactions", key = "#id")
    public TransactionDto getTransactionById(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        return mapToDto(transaction);
    }

    @Override
    public Page<TransactionDto> getUserTransactions(TransactionFilterRequest filterRequest) {

        int page = filterRequest.getPage() != null ? filterRequest.getPage() : 0;
        int size = filterRequest.getSize() != null ? filterRequest.getSize() : 20;
        String sortBy = filterRequest.getSortBy() != null ? filterRequest.getSortBy() : "createdAt";
        String sortDirection = "desc".equalsIgnoreCase(filterRequest.getSortDirection())
                ? "desc" : "asc";

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Get transactions by user ID
        Page<Transaction> transactions = transactionRepository.findBySenderIdOrReceiverId(
                filterRequest.getUserId(),
                filterRequest.getUserId(),
                pageable
        );

        return transactions.map(this::mapToDto);
    }

    @Override
    public List<TransactionDto> getUserTransactionsByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = transactionRepository.findBySenderIdOrReceiverIdAndCreatedAtBetween(
                userId,
                userId,
                startDate,
                endDate
        );

        return transactions.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDto> getWalletTransactions(UUID walletId) {
        List<Transaction> transactions = transactionRepository.findBySenderWalletIdOrReceiverWalletId(
                walletId,
                walletId
        );

        return transactions.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .id(transactionDto.getId())
                .senderId(transactionDto.getSenderId())
                .receiverId(transactionDto.getReceiverId())
                .senderWalletId(transactionDto.getSenderWalletId())
                .receiverWalletId(transactionDto.getReceiverWalletId())
                .amount(transactionDto.getAmount())
                .currencyCode(transactionDto.getCurrencyCode())
                .status(transactionDto.getStatus())
                .type(transactionDto.getType())
                .description(transactionDto.getDescription())
                .createdAt(transactionDto.getCreatedAt())
                .build();

        transactionRepository.save(transaction);
    }

    private TransactionDto mapToDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .senderId(transaction.getSenderId())
                .receiverId(transaction.getReceiverId())
                .senderWalletId(transaction.getSenderWalletId())
                .receiverWalletId(transaction.getReceiverWalletId())
                .amount(transaction.getAmount())
                .currencyCode(transaction.getCurrencyCode())
                .status(transaction.getStatus())
                .type(transaction.getType())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
