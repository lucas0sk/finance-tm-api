package com.tokiomarine.finance.api.controller;

import com.tokiomarine.finance.repository.UserRepository;
import com.tokiomarine.finance.service.CurrentUserService;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public Map<String,Object> me() {
        var u = userRepository.findByUsername(currentUserService.username()).orElseThrow();
        var acc = u.getAccount();
        return Map.of("accountNumber", acc.getNumber(),"balance", acc.getBalance());
    }
}
