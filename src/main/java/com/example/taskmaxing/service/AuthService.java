package com.example.taskmaxing.service;

import com.example.taskmaxing.GlobalErroring.ResourceNotFoundException;
import com.example.taskmaxing.model.dto.request.LoginRequest;
import com.example.taskmaxing.model.dto.request.TokenRefreshRequest;
import com.example.taskmaxing.model.dto.response.AuthResponse;
import com.example.taskmaxing.model.entity.RefreshToken;
import com.example.taskmaxing.model.entity.User;
import com.example.taskmaxing.repository.RefreshTokenRepository;
import com.example.taskmaxing.repository.UserRepository;
import com.example.taskmaxing.secuirity.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final long REFRESH_TOKEN_EXPIRY_DAYS = 7;

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı"));

        // 1. 15 dəqiqəlik Access Token generastiya edirik
        String accessToken = jwtService.generateAccessToken(user);

        // 2. 7 günlük Refresh Token yaradırıq və bazaya yazırıq
        var refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        // 3. Hər ikisini də DTO-ya qoyub qaytarırıq
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }
    @Transactional
    public AuthResponse refresh(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration) // Vaxtı bitibmi yoxla
                .map(RefreshToken::getUser) // Token-dən user-i götür
                .map(user -> {
                    // Təzə access token yaradırıq
                    String newAccessToken = jwtService.generateAccessToken(user);

                    return AuthResponse.builder()
                            .accessToken(newAccessToken)
                            .refreshToken(requestRefreshToken) // Mövcud refresh tokeni geri qaytarırıq
                            .build();
                })
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token bazada tapılmadı!"));
    }

    private String createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(Instant.now().plusSeconds(60L * 60 * 24 * REFRESH_TOKEN_EXPIRY_DAYS))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }
}
