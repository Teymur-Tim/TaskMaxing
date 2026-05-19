package com.example.taskmaxing.service;

import com.example.taskmaxing.mapper.UserMapper;
import com.example.taskmaxing.model.dto.request.CreateUserRequest;
import com.example.taskmaxing.model.dto.response.UserResponse;
import com.example.taskmaxing.model.entity.User;
import com.example.taskmaxing.repository.UserRepository;
import com.example.taskmaxing.secuirity.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;

    public UserResponse createUser(CreateUserRequest createUserRequest) {
        User user = userMapper.toEntity(createUserRequest);

        // Əvvəlcə şifrəni hashləyib obyektə set edirik
        user.setPassword(passwordEncoder.encode(createUserRequest.password()));
        user.setKarmaPoints(0L);
        // İndi isə bircə dəfə və birbaşa təhlükəsiz şəkildə bazaya yazırıq (Tək INSERT sorğusu)
        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }
}
