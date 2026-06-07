package com.example.taskmaxing.model.dto.response;

import java.math.BigDecimal;
import java.time.Instant;


public record ConversationResponse(
        Long taskId,
        String title,
        BigDecimal budget,
        String status,
        String clientName,
        String taskerName,
        Long lastMessageId,
        String lastContent,
        String lastSenderName,
        Instant lastCreatedAt
) { }
