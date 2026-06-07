package com.example.taskmaxing.service;

import com.example.taskmaxing.GlobalErroring.BadRequestException;
import com.example.taskmaxing.GlobalErroring.ResourceNotFoundException;
import com.example.taskmaxing.mapper.AdminUserMapper;
import com.example.taskmaxing.model.dto.response.AdminUserResponse;
import com.example.taskmaxing.model.entity.User;
import com.example.taskmaxing.model.enums.Role;
import com.example.taskmaxing.repository.RefreshTokenRepository;
import com.example.taskmaxing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AdminUserMapper adminUserMapper;

    @Transactional(readOnly = true)
    public List<AdminUserResponse> listUsers() {
        return userRepository.findAllByOrderByUsernameAsc().stream()
                .map(adminUserMapper::toResponse)
                .toList();
    }

    @Transactional
    public AdminUserResponse banUser(String username) {
        User user = getUserOr404(username);
        if (user.getRoles().contains(Role.ADMIN)) {
            throw new BadRequestException("Admini ban edə bilməzsən!");
        }
        user.setBanned(true);
        refreshTokenRepository.deleteByUser(user);
        return adminUserMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public AdminUserResponse unbanUser(String username) {
        User user = getUserOr404(username);
        user.setBanned(false);
        return adminUserMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(String username) {
        User user = getUserOr404(username);
        if (user.getRoles().contains(Role.ADMIN)) {
            throw new BadRequestException("Admin hesabını silə bilməzsən!");
        }
        refreshTokenRepository.deleteByUser(user);
        userRepository.delete(user);
    }

    private User getUserOr404(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı: " + username));
    }
}
