package com.demo.autocareer.config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.demo.autocareer.model.CustomerDetails;
import com.demo.autocareer.model.User;
import com.demo.autocareer.repository.UserRepository;
import com.demo.autocareer.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        
        System.out.println("🔍 Servlet path: " + request.getServletPath()); 
        
        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String email = jwtUtil.extractUsername(token, true);
            System.out.println("🔐 Extracted email from JWT: " + email);

            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null && jwtUtil.validateToken(token, user, true)) {
                String role = jwtUtil.extractRoleFromAccessToken(token);
                System.out.println("🎯 Role from JWT: " + role);

                CustomerDetails customerDetails = new CustomerDetails(user);

                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        customerDetails, // 👈 sử dụng đúng kiểu UserDetails
                        null,
                        customerDetails.getAuthorities()
                    );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }
        }


        filterChain.doFilter(request, response);
    }
}