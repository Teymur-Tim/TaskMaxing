package com.example.taskmaxing.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateReportRequest(
        @NotBlank(message = "Report edilən istifadəçi adı boş ola bilməz!")
        String reportedUsername,
        @NotBlank(message = "Şikayət səbəbi boş ola bilməz!")
        @Size(min = 5, max = 1000, message = "Səbəb 5-1000 simvol arası olmalıdır!")
        String reason
) { }
