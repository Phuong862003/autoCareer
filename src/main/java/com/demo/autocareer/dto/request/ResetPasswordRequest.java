package com.demo.autocareer.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token;
    private String newPassword;

    public String getToken(){
        return token;
    }

    public String getNewPassword(){
        return newPassword;
    }
}