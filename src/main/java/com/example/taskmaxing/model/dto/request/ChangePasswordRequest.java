package com.example.taskmaxing.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "Cari şifrə boş qoyula bilməz!")
        String currentPassword,
        @NotBlank(message = "Yeni şifrə boş qoyula bilməz!")
        @Size(min = 6, message = "Yeni şifrə ən azı 6 simvoldan ibarət olmalıdır!")
        String newPassword
) { }
