package com.tokiomarine.finance.domain.entity;

import com.tokiomarine.finance.domain.TransferStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "transfer", uniqueConstraints = @UniqueConstraint(name = "uk_transfer_request", columnNames = "requestId"))
public class Transfer {

    @Id
    @GeneratedValue
    private Long Id;

    @Column(nullable = false, length = 36)
    private String requestId;

    @ManyToOne(optional = false)
    private Account fromAccount;

    @ManyToOne(optional = false)
    private Account toAccount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate scheduledDate;

    @Column(nullable = false)
    private LocalDate transferDate;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal feeFixed;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal feePercent;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal feeAmount;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransferStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Instant executedAt;

    @Column(length = 200)
    private String failureReason;
}
