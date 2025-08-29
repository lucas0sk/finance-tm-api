package com.tokiomarine.finance.api.controller;

import com.tokiomarine.finance.api.dto.response.AccountSummaryResponse;
import com.tokiomarine.finance.repository.UserRepository;
import com.tokiomarine.finance.service.CurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    public AccountController(CurrentUserService currentUserService, UserRepository userRepository) {
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<AccountSummaryResponse> me() {
        var u = userRepository.findByUsername(currentUserService.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var acc = u.getAccount();
        return ResponseEntity.ok(new AccountSummaryResponse(acc.getNumber(), acc.getBalance()));
    }
}
