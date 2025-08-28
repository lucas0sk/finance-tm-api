package com.tokiomarine.finance.api.controller;

import com.tokiomarine.finance.domain.UserRole;
import com.tokiomarine.finance.repository.AccountRepository;
import com.tokiomarine.finance.repository.UserRepository;
import com.tokiomarine.finance.service.RegistrationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RegistrationService registrationService;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RegistrationService registrationService, UserRepository userRepository, AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.registrationService = registrationService;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            var admin = registrationService.register("Admin Tesouraria", "00000000000", "admin@local",
                    "admin", "admin123", UserRole.ADMIN);
            // Credite saldo na conta admin para usar nas demos
            var acc = admin.getAccount();
            acc.setBalance(new BigDecimal("100000.00"));
            accountRepository.save(acc);
        }
    }
}
