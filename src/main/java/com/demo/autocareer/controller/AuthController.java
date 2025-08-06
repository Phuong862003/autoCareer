package com.demo.autocareer.controller;


import java.io.UnsupportedEncodingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.autocareer.dto.LoginRequest;
import com.demo.autocareer.dto.request.ChangePasswordRequest;
import com.demo.autocareer.dto.request.ForgotPasswordRequest;
import com.demo.autocareer.dto.request.RefreshTokenRequest;
import com.demo.autocareer.dto.request.RegisterDTORequest;
import com.demo.autocareer.dto.request.ResetPasswordRequest;
import com.demo.autocareer.dto.response.JwtAuthResponse;
import com.demo.autocareer.dto.response.ResponseData;
import com.demo.autocareer.model.enums.AccountStatus;
import com.demo.autocareer.model.enums.RoleEnum;
import com.demo.autocareer.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.demo.autocareer.model.User;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseData<?> login(@RequestBody LoginRequest request) {
        return ResponseData.builder()
            .status(HttpStatus.OK.value())
            .message(HttpStatus.OK.getReasonPhrase())
            .data(authService.login(request))
            .build();
    }

    @PostMapping("/refresh-token")
    public ResponseData<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseData.builder()
            .status(HttpStatus.OK.value())
            .message(HttpStatus.OK.getReasonPhrase())
            .data(authService.refreshToken(request))
            .build();
    }

    @PostMapping("/logout")
    public ResponseData<?> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());

        return ResponseData.builder()
            .status(HttpStatus.OK.value())
            .message("LOGOUT SUCCESS")
            .data(null)
            .build();
    }

    @PostMapping("/register")
    public ResponseData<?> register(@RequestBody @Valid RegisterDTORequest registerDTORequest) throws MessagingException, UnsupportedEncodingException {
        return ResponseData.builder()
            .status(HttpStatus.OK.value())
            .message("REGISTER SUCCESS")
            .data(authService.register(registerDTORequest, AccountStatus.PENDING))
            .build();
    }
    
    @GetMapping("/verify")
    public ResponseData<?> verify(@RequestParam String token) {
        boolean result = authService.verifyToken(token);
        return result
            ? ResponseData.builder().status(200).message("Xác thực thành công").build()
            : ResponseData.builder().status(400).message("Token không hợp lệ hoặc đã hết hạn").build();
    }
    
    @PutMapping("/change-password")
    public ResponseData<?> changePassword(@RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseData.builder()
            .status(HttpStatus.OK.value())
            .message("CHANGE PASSWORD SUCCESS")
            .data(null)
            .build();
    }

    @PostMapping("/forgot-password")
    public ResponseData<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.sendResetPasswordEmail(request.getEmail());
        return ResponseData.builder()
            .status(HttpStatus.OK.value())
            .message("Đã gửi email đặt lại mật khẩu")
            .data(null)
            .build();
    }

    @PutMapping("/reset-password")
    public ResponseData<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseData.builder()
            .status(HttpStatus.OK.value())
            .message("Đặt lại mật khẩu thành công")
            .data(null)
            .build();
    }
}
