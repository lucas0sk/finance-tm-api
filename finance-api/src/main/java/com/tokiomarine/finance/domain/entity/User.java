package com.tokiomarine.finance.domain.entity;

import com.tokiomarine.finance.domain.UserRole;
import com.tokiomarine.finance.domain.UserStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Getter
@Setter
@Entity
@Table(
        name = "user",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_user_cpf", columnNames = "cpf")
        },
        indexes = {
                @Index(name = "ix_user_email", columnList = "email"),
                @Index(name = "ix_user_cpf", columnList = "cpf")
        }
)
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false)
    private String cpf;

    @Email
    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false, length = 60)
    private String username;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserStatus status = UserStatus.ACTIVE;

    @OneToOne(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Account account;
}
