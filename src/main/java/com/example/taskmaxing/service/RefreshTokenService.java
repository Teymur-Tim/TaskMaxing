package com.example.taskmaxing.service; // ÖZ PAKET ADINI YAZ (Məsələn: com.example.taskmaxing.service)
import com.example.taskmaxing.GlobalErroring.ConflictException;
import com.example.taskmaxing.GlobalErroring.ResourceNotFoundException;
import com.example.taskmaxing.model.entity.RefreshToken;

import com.example.taskmaxing.repository.RefreshTokenRepository;
import com.example.taskmaxing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public RefreshToken createRefreshToken(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı"));

        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plusMillis(604800000))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new ConflictException("Refresh tokenin vaxtı bitib. Zəhmət olmasa yenidən login olun!");
        }
        return token;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
}