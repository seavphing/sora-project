package com.soramitsu.assignment.transaction_service.controller;

import com.soramitsu.assignment.transaction_service.dto.TransactionDto;
import com.soramitsu.assignment.transaction_service.dto.TransactionFilterRequest;
import com.soramitsu.assignment.transaction_service.service.TransactionExportService;
import com.soramitsu.assignment.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionExportService transactionExportService;

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable String id) {
        TransactionDto transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<TransactionDto>> getUserTransactions(
            @PathVariable Long userId,
            @ModelAttribute TransactionFilterRequest filterRequest) {

        filterRequest.setUserId(userId);
        Page<TransactionDto> transactions = transactionService.getUserTransactions(filterRequest);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<TransactionDto>> getWalletTransactions(@PathVariable UUID walletId) {
        List<TransactionDto> transactions = transactionService.getWalletTransactions(walletId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/export/csv/user/{userId}")
    public ResponseEntity<byte[]> exportTransactionsToCSV(
            @PathVariable Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        byte[] csvBytes = transactionExportService.exportToCSV(userId, startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(csvBytes.length)
                .body(csvBytes);
    }

    @GetMapping("/export/excel/user/{userId}")
    public ResponseEntity<byte[]> exportTransactionsToExcel(
            @PathVariable Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        byte[] excelBytes = transactionExportService.exportToExcel(userId, startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(excelBytes.length)
                .body(excelBytes);
    }
}
