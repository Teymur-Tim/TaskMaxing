package com.example.taskmaxing.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "İstifadəçi adı boş qoyula bilməz!")
        @Size(min = 4, max = 20, message = "İstifadəçi adı 4-20 simvol arası olmalıdır!")
        String username,
        @NotBlank(message = "Email boş qoyula bilməz!")
        @Email(message = "Email formatı yanlışdır!")
        String email,
        @NotBlank(message = "Telefon nömrəsi boş qoyula bilməz!")
        @Pattern(
                regexp = "^\\+?[0-9]{7,15}$",
                message = "Telefon nömrəsi düzgün formatda deyil! (məs: +994501234567)"
        )
        String phoneNumber,
        @NotBlank(message = "Şifrə boş qoyula bilməz!")
        @Size(min = 6, message = "Şifrə ən azı 6 simvoldan ibarət olmalıdır!")
        String password,
        String bio
) { }
