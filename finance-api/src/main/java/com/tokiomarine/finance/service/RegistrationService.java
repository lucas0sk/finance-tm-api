package com.tokiomarine.finance.service;

import com.tokiomarine.finance.domain.UserRole;
import com.tokiomarine.finance.domain.entity.Account;
import com.tokiomarine.finance.domain.entity.User;
import com.tokiomarine.finance.repository.AccountRepository;
import com.tokiomarine.finance.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository userRepository, AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String fullName, String cpf, String email, String username, String rawPassword, UserRole role) {
        if (userRepository.findByUsername(username).isPresent()) throw new IllegalArgumentException("Username already exists");
        var u = new User();
        u.setFullName(fullName);
        u.setCpf(cpf);
        u.setEmail(email);
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setRole(role);
        userRepository.save(u);

        var acc = new Account();
        acc.setOwner(u);
        acc.setNumber(generateUniqueAccountNumber());
        acc.setBalance(BigDecimal.ZERO);
        accountRepository.save(acc);

        u.setAccount(acc);
        return u;
    }

    private String generateUniqueAccountNumber() {
        for (int i = 0; i < 8; i++) {
            String candidate = String.format("%010d", ThreadLocalRandom.current().nextLong(0, 1_000_000_0000L));
            if (!accountRepository.existsByNumber(candidate)) return candidate;
        }
        throw new IllegalStateException("Failed to generate unique account number");
    }
}
