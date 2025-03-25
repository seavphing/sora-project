package com.soramitsu.assignment.gift_code_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gift_codes", schema = "gift_code_schema")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiftCode {
    @Id
    @Column(unique = true, nullable = false, length = 16)
    private String code;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currencyCode;

    @Column(nullable = false)
    private boolean redeemed;

    @Column(nullable = false)
    private Long createdByUserId;

    private Long redeemedByUserId;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime redeemedAt;

    @Version
    private Long version;
}
