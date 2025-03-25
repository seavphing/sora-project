package com.soramitus.assignment.common_lib.events;

import com.soramitus.assignment.common_lib.dto.TransactionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    private TransactionDto transaction;
    private String eventType; // CREATED, COMPLETED, FAILED
}
