package com.example.taskmaxing.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(min = 4, max = 20, message = "İstifadəçi adı 4-20 simvol arası olmalıdır!")
        String username,
        @Email(message = "Email formatı yanlışdır!")
        String email,
        String bio,
        String avatar,
        Boolean phoneVisible
) { }
