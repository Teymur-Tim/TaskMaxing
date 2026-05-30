package com.example.taskmaxing.controller;

import com.example.taskmaxing.model.dto.request.CreateUserRequest;
import com.example.taskmaxing.model.dto.request.LoginRequest;
import com.example.taskmaxing.model.dto.request.TokenRefreshRequest;
import com.example.taskmaxing.model.dto.response.AuthResponse;
import com.example.taskmaxing.model.dto.response.UserResponse;
import com.example.taskmaxing.repository.UserRepository;
import com.example.taskmaxing.service.AuthService;
import com.example.taskmaxing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refresh(@RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}
