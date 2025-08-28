package com.tokiomarine.finance.api.controller;

import com.tokiomarine.finance.service.SettlementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/settlements")
public class SettlementAdminController {

    private final SettlementService settlementService;

    public SettlementAdminController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    //apenas para facilitar apresentação
    @PostMapping("/run-today")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> runToday() {
        int processed = settlementService.settleDue(LocalDate.now());
        return ResponseEntity.ok(Map.of("processed", processed, "date", LocalDate.now().toString()));
    }
}
