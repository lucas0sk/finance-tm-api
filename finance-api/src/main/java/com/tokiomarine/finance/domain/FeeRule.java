package com.tokiomarine.finance.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;

public enum FeeRule {
    SAME_DAY(0, 0, new BigDecimal("3.00"), new BigDecimal("2.50")),
    DAYS_1_TO_10(1, 10, new BigDecimal("12.00"), BigDecimal.ZERO),
    DAYS_11_TO_20(11, 20, BigDecimal.ZERO, new BigDecimal("8.20")),
    DAYS_21_TO_30(21, 30, BigDecimal.ZERO, new BigDecimal("6.90")),
    DAYS_31_TO_40(31, 40, BigDecimal.ZERO, new BigDecimal("4.70")),
    DAYS_41_TO_50(41, 50, BigDecimal.ZERO, new BigDecimal("1.70"));

    private final int startDay;
    private final int endDay;
    @Getter
    private final BigDecimal feeFixed;
    @Getter
    private final BigDecimal feePercent;

    FeeRule(int startDay, int endDay, BigDecimal feeFixed, BigDecimal feePercent) {
        this.startDay = startDay;
        this.endDay = endDay;
        this.feeFixed = feeFixed;
        this.feePercent = feePercent;
    }

    public static FeeRule fromDays(long days) {
        return Arrays.stream(values())
                .filter(r -> days >= r.startDay && days <= r.endDay)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("There is no fee applicable for the chosen date"));
    }
}
