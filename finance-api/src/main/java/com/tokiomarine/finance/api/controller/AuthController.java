package com.tokiomarine.finance.api.controller;

import com.tokiomarine.finance.api.dto.AuthResponse;
import com.tokiomarine.finance.api.dto.LoginRequest;
import com.tokiomarine.finance.api.dto.RegisterRequest;
import com.tokiomarine.finance.service.RegistrationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    @Value("${security.jwt.issuer}") String issuer;
    @Value("${security.jwt.ttl-minutes}") long ttlMinutes;

    public AuthController(RegistrationService registrationService, AuthenticationConfiguration authCfg, JwtEncoder jwtEncoder) throws Exception{
        this.registrationService = registrationService;
        this.authenticationManager = authCfg.getAuthenticationManager();
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        var user = registrationService.register(req.getFullName(), req.getCpf(), req.getEmail(), req.getUsername(), req.getPassword(), req.getRole());

        var token = generateToken(user.getUsername(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponse(token, user.getAccount().getNumber()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        var role = "USER";
        var token = generateToken(req.getUsername(), role);
        return ResponseEntity.ok(new AuthResponse(token, null));
    }

    private String generateToken(String username, String role) {
        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(ttlMinutes, ChronoUnit.MINUTES))
                .subject(username)
                .claim("scope", "ROLE_" + role)
                .build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
