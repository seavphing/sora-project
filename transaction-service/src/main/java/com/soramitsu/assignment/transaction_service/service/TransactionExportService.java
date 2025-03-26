package com.soramitsu.assignment.transaction_service.service;

public interface TransactionExportService {

    public byte[] exportToCSV(Long userId, String startDate, String endDate);

    public byte[] exportToExcel(Long userId, String startDate, String endDate);


}
