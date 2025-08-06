package com.demo.autocareer.dto.request;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String email;  

    public String getEmail() {
        return email;
    }
}
