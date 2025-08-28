package com.tokiomarine.finance.api.dto.request;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class ScheduleTransferRequest {

    @NotNull
    private UUID requestId;

    @NotBlank
    @Pattern(regexp = "\\d{10}")
    private String fromAccount;

    @NotBlank
    @Pattern(regexp = "\\d{10}")
    private String toAccount;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    @FutureOrPresent
    private LocalDate transferDate;
}
