package com.example.taskmaxing.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "İstifadəçi adı boş qoyula bilməz!")
    private String username;
    @NotBlank(message = "Şifrə boş qoyula bilməz!")
    private String password;
}
