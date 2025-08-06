package com.demo.autocareer.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.demo.autocareer.model.CustomerDetails;
import com.demo.autocareer.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtUtil {
     @Value("${application.auth.access-token-secret-key}")
    private String accessTokenSecret;

    @Value("${application.auth.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${application.auth.refresh-token-secret-key}")
    private String refreshTokenSecret;

    @Value("${application.auth.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Value("${application.auth.verification-token-secret-key}")
    private String verificationSecret;

    @Value("${application.auth.verification-token-expiration-ms}")
    private long verificationTokenExpiration;

    public String generateAccessToken(User user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_" + user.getRole().getRoleName().toUpperCase());
        return generateToken(claims, user.getEmail(), accessTokenExpirationMs, accessTokenSecret);
    }

    public String generateRefreshToken(User user) {
        return generateToken(new HashMap<>(), user.getEmail(), refreshTokenExpirationMs, refreshTokenSecret);
    }

    public String generateVerificationToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return generateToken(claims, email, verificationTokenExpiration, verificationSecret);
    }

    public String generateToken(Map<String, Object> claims, String subject, Long expirationMs, String secretKey){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token, boolean isAccessToken){
        try {
            return extractAllClaims(token, isAccessToken ? accessTokenSecret : refreshTokenSecret).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public String extractRoleFromAccessToken(String token){
        return (String) extractAllClaims(token, accessTokenSecret).get("role");
    }
    
    public boolean isTokenExpired(String token, boolean isAccessToken) {
        Date expiration = extractAllClaims(token, isAccessToken ? accessTokenSecret : refreshTokenSecret).getExpiration();
        return expiration.before(new Date());
    }

    public boolean validateToken(String token, User user, boolean  isAccessToken){
        String email = extractUsername(token, isAccessToken);
        return email.equals(user.getEmail()) && !isTokenExpired(token, isAccessToken);
    }

    public boolean validateRefreshToken(String token, User user) {
        String email = extractUsername(token, false);
        return email != null && email.equals(user.getEmail()) && !isTokenExpired(token, false);
    }


    private Claims extractAllClaims(String token, String secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }



    public String getEmailFromVerificationToken(String token) {
        try {
            Claims claims = extractAllClaims(token, verificationSecret);
            System.out.println("Token subject (email): " + claims.getSubject());
            System.out.println("Expiration: " + claims.getExpiration());
            return claims.getSubject();
        } catch (Exception e) {
            System.err.println("Token verification failed: " + e.getMessage());
            return null;
        }
    }

    public boolean isVerificationTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token, verificationSecret);
            System.out.println("Expiration: " + claims.getExpiration());
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // Add to JwtUtil class
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomerDetails userDetails) {
            return userDetails.getUsername(); // chính là email
        }
        throw new RuntimeException("Invalid principal");
    }


}
