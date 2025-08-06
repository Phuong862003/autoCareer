package com.demo.autocareer.service;

import java.io.UnsupportedEncodingException;

import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import com.demo.autocareer.dto.LoginRequest;
import com.demo.autocareer.dto.request.ChangePasswordRequest;
import com.demo.autocareer.dto.request.RefreshTokenRequest;
import com.demo.autocareer.dto.request.RegisterDTORequest;
import com.demo.autocareer.dto.response.JwtAuthResponse;
import com.demo.autocareer.dto.response.RegisterDTOReponse;
import com.demo.autocareer.model.User;
import com.demo.autocareer.model.enums.AccountStatus;

@Service
public interface  AuthService {
    JwtAuthResponse login(LoginRequest request);
    JwtAuthResponse refreshToken(RefreshTokenRequest request);
    void logout(String refreshtoken);
    RegisterDTOReponse register(RegisterDTORequest registerRequestDTO, AccountStatus accountStatus)
            throws MessagingException, UnsupportedEncodingException;
    boolean isValidPassword(String password);        
    void sendVerificaionEmail(String email, String token);
    boolean verifyToken(String token);
    void changePassword(ChangePasswordRequest changePasswordRequest);
    void sendResetPasswordEmail(String email);
    void resetPassword(String token, String newPassword);
}
