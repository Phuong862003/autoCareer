package com.demo.autocareer.service.impl;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.demo.autocareer.service.TokenService;

public class TokenServiceImpl implements TokenService{
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String generateToken(String email) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("verify:" + token, email, 10, TimeUnit.MINUTES);
        return token;
    }

    public String getEmailFromToken(String token) {
        return redisTemplate.opsForValue().get("verify:" + token);
    }

    public void removeToken(String token) {
        redisTemplate.delete("verify:" + token);
    }
}
