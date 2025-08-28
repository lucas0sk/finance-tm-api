package com.tokiomarine.finance.service;

import com.tokiomarine.finance.domain.TransferStatus;
import com.tokiomarine.finance.domain.entity.Transfer;
import com.tokiomarine.finance.repository.AccountRepository;
import com.tokiomarine.finance.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.time.Instant;
import java.time.LocalDate;

@Service
public class SettlementService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;

    public SettlementService(TransferRepository transferRepository, AccountRepository accountRepository) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void settleOne(Transfer t) {
        var from = accountRepository.findById(t.getFromAccount().getId())
                .orElseThrow(() -> new IllegalStateException("Source account not found"));
        var to = accountRepository.findById(t.getToAccount().getId())
                .orElseThrow(() -> new IllegalStateException("Target account not found"));

        var debit = t.getTotalAmount();
        var credit = t.getAmount();

        if(from.getBalance().compareTo(debit) < 0){
            t.setStatus(TransferStatus.FAILED);
            t.setFailureReason("Insufficient balance upon settlement");
            t.setExecutedAt(Instant.now());
            transferRepository.save(t);
            return;
        }

        from.setBalance(from.getBalance().subtract(debit));
        to.setBalance(to.getBalance().add(credit));

        t.setStatus(TransferStatus.SUCCESS);
        t.setExecutedAt(Instant.now());
    }

    @Transactional
    public int settleDue(LocalDate today) {
        var due = transferRepository.findDue(today);
        int ok = 0;
        for (var t : due){
            try {
                settleOne(t);
                ok++;
            } catch (OptimisticLockException e) {

            } catch (RuntimeException e) {
                t.setStatus(TransferStatus.FAILED);
                t.setFailureReason("Error in settlement: " + e.getMessage());
                t.setExecutedAt(Instant.now());
                transferRepository.save(t);
            }
        }
        return ok;
    }
}
