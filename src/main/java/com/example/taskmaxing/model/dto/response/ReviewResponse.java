package com.example.taskmaxing.model.dto.response;

import java.time.Instant;

public record ReviewResponse(
        Long id,
        Integer rating,
        String comment,
        String reviewerName,
        String receiverName,
        Long taskId,
        Instant createdAt
) { }
