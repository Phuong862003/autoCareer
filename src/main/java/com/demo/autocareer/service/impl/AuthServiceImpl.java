package com.demo.autocareer.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.autocareer.dto.LoginRequest;
import com.demo.autocareer.dto.request.ChangePasswordRequest;
import com.demo.autocareer.dto.request.RefreshTokenRequest;
import com.demo.autocareer.dto.request.RegisterDTORequest;
import com.demo.autocareer.dto.response.JwtAuthResponse;
import com.demo.autocareer.dto.response.RegisterDTOReponse;
import com.demo.autocareer.exception.ErrorCode;
import com.demo.autocareer.mapper.RegisterMapper;
import com.demo.autocareer.model.RefreshToken;
import com.demo.autocareer.model.Role;
import com.demo.autocareer.model.User;
import com.demo.autocareer.model.enums.AccountStatus;
import com.demo.autocareer.repository.RefreshTokenRepository;
import com.demo.autocareer.repository.RoleRepository;
import com.demo.autocareer.repository.UserRepository;
import com.demo.autocareer.service.AuthService;
import com.demo.autocareer.utils.ExceptionUtil;
import com.demo.autocareer.utils.JwtUtil;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RegisterMapper registerMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public JwtAuthResponse login(LoginRequest request){
        String email = request.getEmail().trim().toLowerCase();
        String password = request.getPassword();

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (BadCredentialsException ex) {
            throw ExceptionUtil.fromErrorCode(ErrorCode.USER_NOT_FOUND);
        }


        User user = userRepository.findByEmail(email)
             .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.USER_NOT_FOUND));
        
        if (!user.getAccountStatus().equals(AccountStatus.ACTIVE)) {
            throw ExceptionUtil.fromErrorCode(ErrorCode.ACCOUNT_NOT_ACTIVATED);
        }

        
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        RefreshToken tokenEntity = new RefreshToken();
        tokenEntity.setToken(refreshToken);
        tokenEntity.setUser(user);
        refreshTokenRepository.save(tokenEntity);


        return new JwtAuthResponse(accessToken, refreshToken, user.getRole().getRoleName());
    }

    public JwtAuthResponse refreshToken(RefreshTokenRequest request) {
        String token = request.getRefreshToken();

        RefreshToken stored = refreshTokenRepository.findByToken(token)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        User user = stored.getUser();

        if (!jwtUtil.validateRefreshToken(token, user)) {
            throw ExceptionUtil.fromErrorCode(ErrorCode.INVALID_TOKEN);
        }

        String newAccessToken = jwtUtil.generateAccessToken(user);

        return new JwtAuthResponse(newAccessToken, token, user.getRole().getRoleName());

    }

    public void logout(String refreshToken) {
        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        refreshTokenRepository.delete(stored);
    }

    @Override
    public RegisterDTOReponse register(
            RegisterDTORequest registerRequestDTO, AccountStatus accountStatus)
            throws MessagingException, UnsupportedEncodingException {

        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw ExceptionUtil.fromErrorCode(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String password = registerRequestDTO.getPassword();
        if(!isValidPassword(password)){
            throw ExceptionUtil.fromErrorCode(ErrorCode.INVALID_PASSWORD_FORMAT);
        }


        Role role = roleRepository.findById(registerRequestDTO.getRole().getId())
        .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.ROLE_NOT_FOUND));

        if ("STUDENT".equalsIgnoreCase(role.getRoleName())) {
            throw ExceptionUtil.fromErrorCode(ErrorCode.ROLE_NOT_ALLOWED);
        }

        User user = registerMapper.mapRequestToEntity(registerRequestDTO);
        user.setRole(role);
        user.setAccountStatus(accountStatus);
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setEnabled(false); 
        userRepository.save(user);

        String token = jwtUtil.generateVerificationToken(user.getEmail());
        sendVerificaionEmail(user.getEmail(), token);
        return registerMapper.mapEntityToResponse(user);
    }

    @Override
    public void sendVerificaionEmail(String toEmail, String token){
        try {
            String link = "http://localhost:8080/auth/verify?token=" + token;
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Xác thực tài khoản");
            message.setText("Nhấp vào liên kết sau để xác thực tài khoản: " + link);
            mailSender.send(message);
        } catch (Exception e) {
            throw ExceptionUtil.fromErrorCode(ErrorCode.EMAIL_SEND_FAIL);

        }
    }

    @Override
    public boolean verifyToken(String token){
        String email = jwtUtil.getEmailFromVerificationToken(token);
        if(email == null) return false;

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.USER_NOT_FOUND));
        
        user.setEnabled(true);
        user.setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(user);
        return true;
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw ExceptionUtil.fromErrorCode(ErrorCode.INVALID_PASSWORD);
        }

        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw ExceptionUtil.fromErrorCode(ErrorCode.DUPLICATE_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void sendResetPasswordEmail(String email){
        Optional<User> userOpt = userRepository.findByEmail(email.trim().toLowerCase());
        if (userOpt.isEmpty()) {
            throw ExceptionUtil.fromErrorCode(ErrorCode.EMAIL_NOT_FOUND);
        }

        String token = jwtUtil.generateVerificationToken(email);
        String link = "http://localhost:8080/auth/reset-password?token=" + token;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Đặt lại mật khẩu");
            message.setText("Nhấn vào link sau để đặt lại mật khẩu: " + link);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw ExceptionUtil.fromErrorCode(ErrorCode.EMAIL_SEND_FAIL);
        }
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        String email = jwtUtil.getEmailFromVerificationToken(token);
        if (email == null) throw ExceptionUtil.fromErrorCode(ErrorCode.INVALID_TOKEN);

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public boolean isValidPassword(String password) {
        if (password == null) return false;
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$";
        return password.matches(regex);
    }


}