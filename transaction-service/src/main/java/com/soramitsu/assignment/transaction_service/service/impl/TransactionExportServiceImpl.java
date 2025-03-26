package com.soramitsu.assignment.transaction_service.service.impl;

import com.opencsv.CSVWriter;
import com.soramitsu.assignment.transaction_service.dto.TransactionDto;
import com.soramitsu.assignment.transaction_service.service.TransactionExportService;
import com.soramitsu.assignment.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionExportServiceImpl implements TransactionExportService {

    private final TransactionService transactionService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public byte[] exportToCSV(Long userId, String startDate, String endDate) {
        List<TransactionDto> transactions = transactionService.getUserTransactionsByDateRange(
                userId,
                startDate != null ? LocalDateTime.parse(startDate) : LocalDateTime.now().minusMonths(1),
                endDate != null ? LocalDateTime.parse(endDate) : LocalDateTime.now()
        );

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVWriter csvWriter = new CSVWriter(outputStreamWriter)) {

            // Write header
            String[] header = {
                    "Transaction ID", "Type", "Status", "Amount", "Currency",
                    "Sender ID", "Sender Wallet", "Receiver ID", "Receiver Wallet",
                    "Description", "Created At"
            };
            csvWriter.writeNext(header);

            // Write data
            for (TransactionDto transaction : transactions) {
                String[] data = {
                        transaction.getId(),
                        transaction.getType(),
                        transaction.getStatus(),
                        transaction.getAmount().toString(),
                        transaction.getCurrencyCode(),
                        transaction.getSenderId() != null ? transaction.getSenderId().toString() : "",
                        transaction.getSenderWalletId() != null ? transaction.getSenderWalletId().toString() : "",
                        transaction.getReceiverId() != null ? transaction.getReceiverId().toString() : "",
                        transaction.getReceiverWalletId() != null ? transaction.getReceiverWalletId().toString() : "",
                        transaction.getDescription() != null ? transaction.getDescription() : "",
                        transaction.getCreatedAt().format(DATE_FORMATTER)
                };
                csvWriter.writeNext(data);
            }

            csvWriter.flush();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to export transactions to CSV", e);
        }
    }

    @Override
    public byte[] exportToExcel(Long userId, String startDate, String endDate) {
        List<TransactionDto> transactions = transactionService.getUserTransactionsByDateRange(
                userId,
                startDate != null ? LocalDateTime.parse(startDate) : LocalDateTime.now().minusMonths(1),
                endDate != null ? LocalDateTime.parse(endDate) : LocalDateTime.now()
        );

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Transactions");

            // Create header row
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            String[] columns = {
                    "Transaction ID", "Type", "Status", "Amount", "Currency",
                    "Sender ID", "Sender Wallet", "Receiver ID", "Receiver Wallet",
                    "Description", "Created At"
            };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            // Fill data rows
            int rowNum = 1;
            for (TransactionDto transaction : transactions) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(transaction.getId());
                row.createCell(1).setCellValue(transaction.getType());
                row.createCell(2).setCellValue(transaction.getStatus());
                row.createCell(3).setCellValue(transaction.getAmount().doubleValue());
                row.createCell(4).setCellValue(transaction.getCurrencyCode());
                row.createCell(5).setCellValue(transaction.getSenderId() != null ? transaction.getSenderId() : 0);
                row.createCell(6).setCellValue(transaction.getSenderWalletId() != null ? transaction.getSenderWalletId().toString() : "");
                row.createCell(7).setCellValue(transaction.getReceiverId() != null ? transaction.getReceiverId() : 0);
                row.createCell(8).setCellValue(transaction.getReceiverWalletId() != null ? transaction.getReceiverWalletId().toString() : "");                row.createCell(9).setCellValue(transaction.getDescription() != null ? transaction.getDescription() : "");
                row.createCell(10).setCellValue(transaction.getCreatedAt().format(DATE_FORMATTER));
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to export transactions to Excel", e);
        }
    }
}
