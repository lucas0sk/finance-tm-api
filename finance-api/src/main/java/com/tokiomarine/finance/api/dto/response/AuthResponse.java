package com.tokiomarine.finance.api.dto.response;

import lombok.Value;

@Value
public class AuthResponse {
    String accessToken;
    String accountNumber;
}
