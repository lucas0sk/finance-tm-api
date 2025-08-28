package com.tokiomarine.finance.service;

import com.tokiomarine.finance.domain.UserRole;
import com.tokiomarine.finance.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String username(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public String accountNumber(){
        var u = userRepository.findByUsername(username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return u.getAccount().getNumber();
    }

    public boolean hasRole(UserRole role){
        return userRepository.hasRole(role);
    }
}
