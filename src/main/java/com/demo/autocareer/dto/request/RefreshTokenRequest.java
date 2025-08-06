package com.demo.autocareer.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data

public class RefreshTokenRequest{
    private String refreshToken;

    public RefreshTokenRequest(){}
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
