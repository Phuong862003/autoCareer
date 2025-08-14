package com.demo.autocareer.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
import com.demo.autocareer.mapper.OrganizationMapper;
import com.demo.autocareer.mapper.RegisterMapper;
import com.demo.autocareer.model.District;
import com.demo.autocareer.model.Organization;
import com.demo.autocareer.model.Province;
import com.demo.autocareer.model.RefreshToken;
import com.demo.autocareer.model.Role;
import com.demo.autocareer.model.User;
import com.demo.autocareer.model.enums.AccountStatus;
import com.demo.autocareer.model.enums.OrganizationType;
import com.demo.autocareer.model.enums.RoleEnum;
import com.demo.autocareer.repository.DistrictRepository;
import com.demo.autocareer.repository.OrganizationRepository;
import com.demo.autocareer.repository.ProvinceRepository;
import com.demo.autocareer.repository.RefreshTokenRepository;
import com.demo.autocareer.repository.RoleRepository;
import com.demo.autocareer.repository.UserRepository;
import com.demo.autocareer.service.AuthService;
import com.demo.autocareer.utils.ExceptionUtil;
import com.demo.autocareer.utils.JwtUtil;

import jakarta.mail.internet.MimeMessage;

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

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private OrganizationMapper organizationMapper;

    @Autowired
    private OrganizationRepository organizationRepository;

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

        if (!registerRequestDTO.getPassword().equals(registerRequestDTO.getRepeatPassword())) {
            throw ExceptionUtil.fromErrorCode(ErrorCode.PASSWORDS_DO_NOT_MATCH);
        }

        String password = registerRequestDTO.getPassword();
        if(!isValidPassword(password)){
            throw ExceptionUtil.fromErrorCode(ErrorCode.INVALID_PASSWORD_FORMAT);
        }
        
        Province province = provinceRepository.findById(registerRequestDTO.getProvinceId())
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.PROVINCE_NOT_FOUND));
        
        District district = districtRepository.findById(registerRequestDTO.getDistrictId())
        .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.DISTRICT_NOT_FOUND));

        if (!district.getProvince().getId().equals(province.getId())) {
            throw ExceptionUtil.fromErrorCode(ErrorCode.DISTRICT_NOT_IN_PROVINCE);
        }

        // Role role = roleRepository.findById(registerRequestDTO.getRole().getId())
        // .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.ROLE_NOT_FOUND));

        // if ("STUDENT".equalsIgnoreCase(role.getRoleName())) {
        //     throw ExceptionUtil.fromErrorCode(ErrorCode.ROLE_NOT_ALLOWED);
        // }
        Role role = null;
        if (registerRequestDTO.getOrganizationName() == null || registerRequestDTO.getOrganizationName().trim().isEmpty()){
            throw ExceptionUtil.fromErrorCode(ErrorCode.ORGANIZATION_NAME_REQUIRED);
        }
        if (registerRequestDTO.getOrganizationType() == OrganizationType.UNIVERSITY) {
            role = roleRepository.findByRoleName("ADMIN_UNIVERSITY")
                    .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.ROLE_NOT_FOUND));
        } else if (registerRequestDTO.getOrganizationType() == OrganizationType.COMPANY) {
            role = roleRepository.findByRoleName("ADMIN_COMPANY")
                    .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.ROLE_NOT_FOUND));
        }

        User user = registerMapper.mapRequestToEntity(registerRequestDTO);
        user.setRole(role);
        user.setAccountStatus(accountStatus);
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setEnabled(false); 
        user.setDistrict(district);
        userRepository.save(user);

        Organization org = organizationMapper.mapRequestToEntity(registerRequestDTO, user);
        org.setOrganizationName(registerRequestDTO.getOrganizationName());
        org.setOrganizationType(registerRequestDTO.getOrganizationType());
        organizationRepository.save(org);

        String token = jwtUtil.generateVerificationToken(user.getEmail());
        sendVerificaionEmail(user.getEmail(), token);
        return registerMapper.mapEntityToResponse(user);
    }

    @Override
    public void sendVerificaionEmail(String toEmail, String token) {
        try {
            String link = "http://localhost:8080/api/auth/verify?token=" + token;

            // Tạo email HTML
            String htmlContent = """
                <html>
                <body style="font-family: Arial, sans-serif; color: #333; line-height: 1.5;">
                    <h2 style="color: #4CAF50;">Xác thực tài khoản của bạn</h2>
                    <p>Xin chào,</p>
                    <p>Cảm ơn bạn đã đăng ký tài khoản. Vui lòng nhấp vào nút dưới đây để hoàn tất xác thực:</p>
                    <a href="%s" style="display: inline-block; padding: 10px 20px; color: white; background-color: #4CAF50; text-decoration: none; border-radius: 5px;">
                        Xác nhận tài khoản
                    </a>
                    <p>Trân trọng,<br/>Đội ngũ hỗ trợ</p>
                </body>
                </html>
            """.formatted(link, link, link);

            // Tạo MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Xác thực tài khoản");
            helper.setText(htmlContent, true); // true => nội dung HTML

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
    public void sendResetPasswordEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email.trim().toLowerCase());
        if (userOpt.isEmpty()) {
            throw ExceptionUtil.fromErrorCode(ErrorCode.EMAIL_NOT_FOUND);
        }

        String token = jwtUtil.generateVerificationToken(email);
        String link = "http://localhost:8080/api/auth/reset-password-link?token=" + token; 

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Đặt lại mật khẩu");

            String content = "<div style=\"font-family: Arial, sans-serif; font-size: 14px; color: #333;\">"
                    + "<p>Xin chào,</p>"
                    + "<p>Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng nhấn nút bên dưới để tiếp tục:</p>"
                    + "<p style=\"text-align: center;\">"
                    + "    <a href=\"" + link + "\" style=\"display: inline-block; background-color: #4CAF50; "
                    + "       color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;\">"
                    + "       Đặt lại mật khẩu</a>"
                    + "</p>"
                    + "<p>Nếu bạn không yêu cầu, vui lòng bỏ qua email này.</p>"
                    + "<br>"
                    + "<p>Trân trọng,</p>"
                    + "<p><b>Đội ngũ hỗ trợ</b></p>"
                    + "</div>";

            helper.setText(content, true); // true = gửi dạng HTML

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw ExceptionUtil.fromErrorCode(ErrorCode.EMAIL_SEND_FAIL);
        }
    }


    @Override
    public void resetPassword(String token, String newPassword, String repeatPassword) {
        String email = jwtUtil.getEmailFromVerificationToken(token);
        if (email == null) throw ExceptionUtil.fromErrorCode(ErrorCode.INVALID_TOKEN);

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.USER_NOT_FOUND));
        
        if(!isValidPassword(newPassword)){
            throw ExceptionUtil.fromErrorCode(ErrorCode.INVALID_PASSWORD_FORMAT);
        }
        
         if (!newPassword.equals(repeatPassword)) {
            throw ExceptionUtil.fromErrorCode(ErrorCode.PASSWORDS_DO_NOT_MATCH);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public boolean isValidPassword(String password) {
        if (password == null) return false;
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$";
        return password.matches(regex);
    }


}