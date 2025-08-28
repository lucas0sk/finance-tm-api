package com.tokiomarine.finance.job;

import com.tokiomarine.finance.service.SettlementService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SettlementJob {

    private final SettlementService settlementService;

    public SettlementJob(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @Scheduled(cron = "${transfers.settlement.cron}")
    public void run() {
        settlementService.settleDue(LocalDate.now());
    }
}
