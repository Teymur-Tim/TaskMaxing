package com.example.taskmaxing.model.dto.response;

import java.time.Instant;

// Çat mesajı cavabı. "Mənimdirmi?" qərarı frontend-də senderName ilə cari
// istifadəçi adının müqayisəsi ilə verilir (review-larda olduğu kimi).
public record MessageResponse(
        Long id,
        Long taskId,
        Long senderId,
        String senderName,
        String content,
        Instant createdAt
) { }
