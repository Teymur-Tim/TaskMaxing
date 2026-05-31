package com.example.taskmaxing.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "İstifadəçi adı boş qoyula bilməz!")
        @Size(min = 4, max = 20, message = "İstifadəçi adı 4-20 simvol arası olmalıdır!")
        String username,
        @NotBlank(message = "Email boş qoyula bilməz!")
        @Email(message = "Email formatı yanlışdır!")
        String email,
        @NotBlank(message = "Şifrə boş qoyula bilməz!")
        @Size(min = 6, message = "Şifrə ən azı 6 simvoldan ibarət olmalıdır!")
        String password,
        String bio
) { }
