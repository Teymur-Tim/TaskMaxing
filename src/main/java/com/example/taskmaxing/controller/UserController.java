package com.example.taskmaxing.controller;

import com.example.taskmaxing.model.dto.request.ChangePasswordRequest;
import com.example.taskmaxing.model.dto.request.CreateUserRequest;
import com.example.taskmaxing.model.dto.request.LoginRequest;
import com.example.taskmaxing.model.dto.request.TokenRefreshRequest;
import com.example.taskmaxing.model.dto.request.UpdateUserRequest;
import com.example.taskmaxing.model.dto.response.AuthResponse;
import com.example.taskmaxing.model.dto.response.UserResponse;
import com.example.taskmaxing.repository.UserRepository;
import com.example.taskmaxing.service.AuthService;
import com.example.taskmaxing.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    // Cari istifadəçinin profili (token-dəki istifadəçi) — avatar/email/bio daxil.
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication.getName()));
    }

    // Yalnız öz profilini yeniləyə bilər (token-dəki istifadəçi). Partial update.
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(userService.updateProfile(authentication.getName(), request));
    }

    // Şifrə dəyişdirilməsi: cari şifrə doğrulandıqdan sonra yeni şifrə yazılır.
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ) {
        userService.changePassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }

    // Leaderboard: ən çox karma toplayan istifadəçilər (rating sıralaması)
    @GetMapping("/leaderboard")
    public ResponseEntity<List<UserResponse>> leaderboard() {
        return ResponseEntity.ok(userService.getLeaderboard());
    }

    // Hər hansı istifadəçinin ictimai profili (email-siz). "me"/"leaderboard" kimi
    // literal yollar daha spesifik olduğu üçün bu {username} onları kölgələmir.
    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> publicProfile(@PathVariable String username) {
        return ResponseEntity.ok(userService.getPublicProfile(username));
    }

    // Yalnız öz hesabını silə bilər (token-dəki istifadəçi). Soft delete olunur.
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(Authentication authentication) {
        userService.deleteAccount(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
