package com.tokiomarine.finance.repository;

import com.tokiomarine.finance.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByNumber(String number);
    boolean existsByNumber(String number);

}
