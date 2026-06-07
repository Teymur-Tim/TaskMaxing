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
import com.example.taskmaxing.model.enums.Role;
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
        if (userRepository.existsByUsername(createUserRequest.username())) {
            throw new ConflictException("Bu istifadəçi adı artıq mövcuddur!");
        }
        if (userRepository.existsByEmail(createUserRequest.email())) {
            throw new ConflictException("Bu email artıq qeydiyyatdan keçib!");
        }
        if (userRepository.existsByPhoneNumber(createUserRequest.phoneNumber())) {
            throw new ConflictException("Bu telefon nömrəsi artıq qeydiyyatdan keçib!");
        }

        User user = userMapper.toEntity(createUserRequest);

        user.setPassword(passwordEncoder.encode(createUserRequest.password()));
        user.setKarmaPoints(0L);
        user.getRoles().add(Role.CLIENT);
        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public UserResponse updateProfile(String username, UpdateUserRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı!"));

        if (request.username() != null && !request.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.username())) {
                throw new ConflictException("Bu istifadəçi adı artıq mövcuddur!");
            }
            user.setUsername(request.username());
        }

        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new ConflictException("Bu email artıq qeydiyyatdan keçib!");
            }
            user.setEmail(request.email());
        }

        if (request.bio() != null) {
            user.setBio(request.bio());
        }

        if (request.avatar() != null) {
            user.setAvatar(request.avatar().isBlank() ? null : request.avatar());
        }

        if (request.phoneVisible() != null) {
            user.setPhoneVisible(request.phoneVisible());
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı!"));
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getPublicProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı: " + username));
        UserResponse full = userMapper.toResponse(user);
        String publicPhone = user.isPhoneVisible() ? full.phoneNumber() : null;
        return new UserResponse(
                full.id(), full.username(), null, publicPhone, full.phoneVisible(), full.bio(),
                full.karmaPoints(), full.avatar(), full.ratingAverage(), full.ratingCount());
    }

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

    @Transactional
    public void deleteAccount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı!"));

        refreshTokenRepository.deleteByUser(user);

        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getLeaderboard() {
        return userRepository.findAllByOrderByKarmaPointsDesc().stream()
                .map(userMapper::toResponse)
                .toList();
    }
}
