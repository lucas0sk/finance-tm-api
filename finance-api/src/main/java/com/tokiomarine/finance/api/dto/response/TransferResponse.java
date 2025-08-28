package com.tokiomarine.finance.api.dto.response;

import lombok.Value;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Value
@Builder
public class TransferResponse {
    String requestId;
    String fromAccount;
    String toAccount;
    BigDecimal amount;
    LocalDate scheduledDate;
    LocalDate transferDate;
    BigDecimal feeFixed;
    BigDecimal feePercent;
    BigDecimal feeAmount;
    BigDecimal totalAmount;
    String status;
    Instant createdAt;
}
