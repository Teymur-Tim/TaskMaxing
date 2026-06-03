package com.example.taskmaxing.service;

import com.example.taskmaxing.GlobalErroring.BadRequestException;
import com.example.taskmaxing.GlobalErroring.ConflictException;
import com.example.taskmaxing.GlobalErroring.ResourceNotFoundException;
import com.example.taskmaxing.mapper.UserMapper;
import com.example.taskmaxing.model.dto.request.ChangePasswordRequest;
import com.example.taskmaxing.model.dto.request.CreateUserRequest;
import com.example.taskmaxing.model.dto.request.UpdateUserRequest;
import com.example.taskmaxing.model.dto.response.UserResponse;
import com.example.taskmaxing.model.entity.User;
import com.example.taskmaxing.repository.RefreshTokenRepository;
import com.example.taskmaxing.repository.UserRepository;
import com.example.taskmaxing.secuirity.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private RefreshTokenRepository refreshTokenRepository;

    public UserResponse createUser(CreateUserRequest createUserRequest) {
        // Bazaya yazmadan əvvəl təkrarlanmanı yoxlayırıq ki, xam DB xətası client-ə getməsin
        if (userRepository.existsByUsername(createUserRequest.username())) {
            throw new ConflictException("Bu istifadəçi adı artıq mövcuddur!");
        }
        if (userRepository.existsByEmail(createUserRequest.email())) {
            throw new ConflictException("Bu email artıq qeydiyyatdan keçib!");
        }
        // Bir nömrə ilə yalnız bir hesab: təkrar nömrə ilə qeydiyyatın qarşısını alırıq.
        if (userRepository.existsByPhoneNumber(createUserRequest.phoneNumber())) {
            throw new ConflictException("Bu telefon nömrəsi artıq qeydiyyatdan keçib!");
        }

        User user = userMapper.toEntity(createUserRequest);

        // Əvvəlcə şifrəni hashləyib obyektə set edirik
        user.setPassword(passwordEncoder.encode(createUserRequest.password()));
        user.setKarmaPoints(0L);
        // İndi isə bircə dəfə və birbaşa təhlükəsiz şəkildə bazaya yazırıq (Tək INSERT sorğusu)
        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    // Profil yenilənməsi: yalnız token-dəki istifadəçi öz məlumatlarını dəyişə bilər.
    // Partial update — yalnız göndərilən (null olmayan) sahələr yenilənir.
    @Transactional
    public UserResponse updateProfile(String username, UpdateUserRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı!"));

        // username dəyişdirilirsə, başqasının istifadə etmədiyini yoxlayırıq
        if (request.username() != null && !request.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.username())) {
                throw new ConflictException("Bu istifadəçi adı artıq mövcuddur!");
            }
            user.setUsername(request.username());
        }

        // email dəyişdirilirsə, başqasının istifadə etmədiyini yoxlayırıq
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new ConflictException("Bu email artıq qeydiyyatdan keçib!");
            }
            user.setEmail(request.email());
        }

        if (request.bio() != null) {
            user.setBio(request.bio());
        }

        // Profil şəkli: "" göndərilərsə şəkil silinir, null isə toxunulmur.
        if (request.avatar() != null) {
            user.setAvatar(request.avatar().isBlank() ? null : request.avatar());
        }

        // Telefon nömrəsinin görünürlüyü (gizlət/göstər) — yalnız göndərildikdə dəyişir.
        if (request.phoneVisible() != null) {
            user.setPhoneVisible(request.phoneVisible());
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    // Cari istifadəçinin profil məlumatları (token-dəki istifadəçi).
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı!"));
        return userMapper.toResponse(user);
    }

    // Hər hansı istifadəçinin ictimai profili (başqasının reytinqinə/bio-suna baxmaq üçün).
    // Email gizli saxlanılır — yalnız ad, bio, avatar, karma və reytinq qaytarılır.
    @Transactional(readOnly = true)
    public UserResponse getPublicProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı: " + username));
        UserResponse full = userMapper.toResponse(user);
        // email həmişə gizli; telefon nömrəsi isə yalnız istifadəçi gizlətməyibsə (phoneVisible) göstərilir.
        String publicPhone = user.isPhoneVisible() ? full.phoneNumber() : null;
        return new UserResponse(
                full.id(), full.username(), null, publicPhone, full.phoneVisible(), full.bio(),
                full.karmaPoints(), full.avatar(), full.ratingAverage(), full.ratingCount());
    }

    // Şifrə dəyişdirilməsi: əvvəlcə cari şifrə doğrulanır, sonra yeni şifrə hashlənib yazılır.
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı!"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadRequestException("Cari şifrə yanlışdır!");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BadRequestException("Yeni şifrə cari şifrə ilə eyni ola bilməz!");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    // Hesabın soft-delete edilməsi: bazadan silinmir, @SoftDelete sayəsində "deleted = true" olur.
    @Transactional
    public void deleteAccount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı!"));

        // Təhlükəsizlik: hesab bağlananda bütün aktiv refresh token-ləri ləğv edirik (real silinir).
        refreshTokenRepository.deleteByUser(user);

        // @SoftDelete sayəsində bu delete əslində "UPDATE users SET deleted = true" sorğusuna çevrilir.
        userRepository.delete(user);
    }

    // Leaderboard: istifadəçiləri karma xalına görə azalan sıra ilə qaytarır.
    @Transactional(readOnly = true)
    public List<UserResponse> getLeaderboard() {
        return userRepository.findAllByOrderByKarmaPointsDesc().stream()
                .map(userMapper::toResponse)
                .toList();
    }
}
