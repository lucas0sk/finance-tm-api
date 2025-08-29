package com.tokiomarine.finance.api.controller;

import com.tokiomarine.finance.api.dto.response.AuthResponse;
import com.tokiomarine.finance.api.dto.request.LoginRequest;
import com.tokiomarine.finance.api.dto.request.RegisterRequest;
import com.tokiomarine.finance.domain.UserRole;
import com.tokiomarine.finance.repository.UserRepository;
import com.tokiomarine.finance.service.RegistrationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
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
@RequestMapping("/api/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    @Value("${security.jwt.issuer}") String issuer;
    @Value("${security.jwt.ttl-minutes}") long ttlMinutes;

    public AuthController(RegistrationService registrationService, AuthenticationConfiguration authCfg, JwtEncoder jwtEncoder, UserRepository userRepository) throws Exception{
        this.registrationService = registrationService;
        this.authenticationManager = authCfg.getAuthenticationManager();
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        var user = registrationService.register(req.getFullName(), req.getCpf(), req.getEmail(), req.getUsername(), req.getPassword(), UserRole.USER);

        var token = generateToken(user.getUsername(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponse(token, user.getAccount().getNumber(), user.getRole().toString()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        var user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var role = user.getRole().name();
        var token = generateToken(user.getUsername(), role);
        return ResponseEntity.ok(new AuthResponse(token, user.getAccount().getNumber(), user.getRole().toString()));
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
        var headers = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(headers, claims))
                .getTokenValue();
    }
}
