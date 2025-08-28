package com.tokiomarine.finance.api.dto.request;

import com.tokiomarine.finance.domain.UserRole;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RegisterRequest {
    @NotBlank
    private String fullName;

    @NotBlank
    private String cpf;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotNull
    private UserRole role;
}
