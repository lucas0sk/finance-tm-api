package com.tokiomarine.finance.api.controller;

import com.tokiomarine.finance.api.dto.request.ScheduleTransferRequest;
import com.tokiomarine.finance.api.dto.response.TransferResponse;
import com.tokiomarine.finance.domain.TransferStatus;
import com.tokiomarine.finance.domain.UserRole;
import com.tokiomarine.finance.domain.entity.Transfer;
import com.tokiomarine.finance.repository.TransferRepository;
import com.tokiomarine.finance.service.CurrentUserService;
import com.tokiomarine.finance.service.TransferService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;
    private final TransferRepository transferRepository;
    private final CurrentUserService currentUserService;

    public TransferController(TransferService transferService, TransferRepository transferRepository, CurrentUserService currentUserService) {
        this.transferService = transferService;
        this.transferRepository = transferRepository;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/schedule")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransferResponse> schedule(@Valid @RequestBody ScheduleTransferRequest req) {
        String myAcc = currentUserService.accountNumber();
        boolean isAdmin = currentUserService.hasRole(UserRole.ADMIN);

        if(!isAdmin){
            req.setFromAccount(myAcc);
        }

        Transfer t = transferService.schedule(req.getRequestId(), req.getFromAccount(), req.getToAccount(), req.getAmount(), req.getTransferDate());
        TransferResponse body = toResponse(t);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/user/extract")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<TransferResponse>> userTransfers(
            @RequestParam(required = false) TransferStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        String myAcc = currentUserService.accountNumber();
        var page = transferRepository.searchByAccount(myAcc, status, startDate, endDate, pageable)
                .map(t -> toResponseForUser(t, myAcc));
        return ResponseEntity.ok(page);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransferResponse>> listAll(
            @RequestParam(required = false) String fromAccount,
            @RequestParam(required = false) String toAccount,
            @RequestParam(required = false) TransferStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        var page = transferRepository.search(fromAccount, toAccount, status, startDate, endDate, pageable)
                .map(this::toResponse);
        return ResponseEntity.ok(page);
    }

    private TransferResponse toResponse(Transfer t) {
        return TransferResponse.builder()
            .requestId(t.getRequestId())
            .fromAccount(mask(t.getFromAccount().getNumber()))
            .toAccount(mask(t.getToAccount().getNumber()))
            .amount(t.getAmount())
            .scheduledDate(t.getScheduledDate())
            .transferDate(t.getTransferDate())
            .feeFixed(t.getFeeFixed())
            .feePercent(t.getFeePercent())
            .feeAmount(t.getFeeAmount())
            .totalAmount(t.getTotalAmount())
            .status(t.getStatus().name())
            .createdAt(t.getCreatedAt())
            .build();
    }

    private TransferResponse toResponseForUser(Transfer t, String myAcc) {
        return TransferResponse.builder()
            .requestId(t.getRequestId())
            .fromAccount(
                t.getFromAccount().getNumber().equals(myAcc)
                    ? t.getFromAccount().getNumber()
                    : mask(t.getFromAccount().getNumber())
                )
            .toAccount(
                t.getToAccount().getNumber().equals(myAcc)
                    ? t.getToAccount().getNumber()
                    : mask(t.getToAccount().getNumber())
                )
            .amount(t.getAmount())
            .scheduledDate(t.getScheduledDate())
            .transferDate(t.getTransferDate())
            .feeFixed(t.getFeeFixed())
            .feePercent(t.getFeePercent())
            .feeAmount(t.getFeeAmount())
            .totalAmount(t.getTotalAmount())
            .status(t.getStatus().name())
            .createdAt(t.getCreatedAt())
            .build();
    }

    private String mask(String n) { return (n != null && n.length()==10) ? "******" + n.substring(6) : n; }
}
