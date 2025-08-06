package com.demo.autocareer.service;

import org.springframework.stereotype.Service;

@Service
public interface TokenService {
    void removeToken(String token);
    String generateToken(String email);
    String getEmailFromToken(String token);
}
