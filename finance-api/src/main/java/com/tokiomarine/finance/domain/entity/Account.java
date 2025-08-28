package com.tokiomarine.finance.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(
        name = "account",
        indexes = {
            @Index(name = "ix_account_user", columnList = "owner_id"),
            @Index(name = "ix_account_number", columnList = "account_number")
        },
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_account_number", columnNames = "account_number")
        }
)
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_account_owner"))
    private User owner;

    @Column(name = "account_number", nullable = false, length = 10)
    private String number;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Version
    private Long version;
}
