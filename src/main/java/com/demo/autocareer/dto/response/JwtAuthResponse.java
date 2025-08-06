package com.demo.autocareer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class JwtAuthResponse {

    public JwtAuthResponse(String accessToken1, String refreshToken1, String role1) {
        this.accessToken = accessToken1;
        this.refreshToken = refreshToken1;
        this.role = role1;
    }

    public JwtAuthResponse(String accessToken1, String refreshToken1) {
        this.accessToken = accessToken1;
        this.refreshToken = refreshToken1;
    }

    private String accessToken;
    private String refreshToken;
    private String role;
}
