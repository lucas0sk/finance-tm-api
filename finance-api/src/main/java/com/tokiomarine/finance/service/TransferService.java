package com.tokiomarine.finance.service;

import com.tokiomarine.finance.domain.FeeRule;
import com.tokiomarine.finance.domain.TransferStatus;
import com.tokiomarine.finance.domain.entity.Account;
import com.tokiomarine.finance.domain.entity.Transfer;
import com.tokiomarine.finance.repository.AccountRepository;
import com.tokiomarine.finance.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;

    public TransferService(AccountRepository accountRepository, TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public Transfer schedule(UUID requestId, String fromNumber, String toNumber, BigDecimal amount, LocalDate transferDate) {
        Optional<Transfer> existing = transferRepository.findByRequestId(requestId.toString());
        if (existing.isPresent()) return existing.get(); //idempotência

        Account from = accountRepository.findByNumber(fromNumber)
                .orElseThrow(() -> new NoSuchElementException("Source account not found"));
        Account to = accountRepository.findByNumber(toNumber)
                .orElseThrow(() -> new NoSuchElementException("Target account not found"));

        if (from.getId().equals(to.getId())) throw new IllegalArgumentException("Accounts cannot be the same");
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Invalid value");

        amount = amount.setScale(2, RoundingMode.HALF_EVEN);

        LocalDate today = LocalDate.now();
        if (transferDate.isBefore(today)) throw new IllegalArgumentException("Transfer date in the past");

        Long days = ChronoUnit.DAYS.between(today, transferDate);

        FeeRule rule = FeeRule.fromDays(days);
        BigDecimal feeFixed = rule.getFeeFixed();
        BigDecimal feePercent = rule.getFeePercent();

        BigDecimal feeAmount = feeFixed.add(
                amount.multiply(feePercent).divide(new BigDecimal("100"), 2, RoundingMode.HALF_EVEN));
        BigDecimal totalAmount = amount.add(feeAmount).setScale(2, RoundingMode.HALF_EVEN);

        Transfer t = new Transfer();
        t.setRequestId(requestId.toString());
        t.setFromAccount(from);
        t.setToAccount(to);
        t.setAmount(amount);
        t.setScheduledDate(today);
        t.setTransferDate(transferDate);
        t.setFeeFixed(feeFixed);
        t.setFeePercent(feePercent);
        t.setFeeAmount(feeAmount);
        t.setTotalAmount(totalAmount);
        t.setStatus(TransferStatus.PENDING);
        t.setCreatedAt(Instant.now());

        return transferRepository.save(t);
    }
}
