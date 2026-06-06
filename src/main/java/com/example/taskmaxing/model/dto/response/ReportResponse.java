package com.example.taskmaxing.model.dto.response;

import com.example.taskmaxing.model.enums.ReportStatus;

import java.time.Instant;

public record ReportResponse(
        Long id,
        String reporterUsername,
        String reportedUsername,
        String reason,
        ReportStatus status,
        Instant createdAt
) { }
