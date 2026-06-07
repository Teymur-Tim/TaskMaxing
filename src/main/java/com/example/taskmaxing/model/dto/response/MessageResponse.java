package com.example.taskmaxing.model.dto.response;

import java.time.Instant;


public record MessageResponse(
        Long id,
        Long taskId,
        Long senderId,
        String senderName,
        String content,
        Instant createdAt
) { }
