package com.tokiomarine.finance.repository;

import com.tokiomarine.finance.domain.TransferStatus;
import com.tokiomarine.finance.domain.entity.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    Optional<Transfer> findByRequestId(String requestId);
    @Query("select t from Transfer t " +
            "where (:fromNumber is null or t.fromAccount.number = :fromNumber) " +
            "and (:toNumber   is null or t.toAccount.number   = :toNumber) " +
            "and (:status     is null or t.status = :status) " +
            "and (:startDate  is null or t.transferDate >= :startDate) " +
            "and (:endDate    is null or t.transferDate <= :endDate)")
    Page<Transfer> search(String fromNumber, String toNumber, TransferStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
