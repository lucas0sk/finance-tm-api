package com.tokiomarine.finance.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;

@Value
@AllArgsConstructor
public class AccountSummaryResponse {
    String accountNumber;
    BigDecimal balance;
}
