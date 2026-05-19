package com.example.taskmaxing.service;

import com.example.taskmaxing.model.dto.request.LoginRequest;
import com.example.taskmaxing.model.dto.response.AuthResponse;
import com.example.taskmaxing.repository.UserRepository;
import com.example.taskmaxing.secuirity.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService  {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;


    public AuthResponse login(LoginRequest request) {
        // 1. Spring Security şifrəni və istifadəçini bazadan tapıb avtomatik yoxlayır
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. Əgər yuxarıdakı addımda xəta çıxmadısa, deməli şifrə düzdür. User-i bazadan götürürük
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("İstifadəçi tapılmadı: " + request.getUsername()));

        // 3. İstifadəçi üçün JWT token yaradırıq
        String token = jwtService.generateToken(user);

        // 4. Tokeni cavab obyektinə büküb geri qaytarırıq
        return new AuthResponse(token);
    }}