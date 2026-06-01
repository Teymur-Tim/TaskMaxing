package com.example.taskmaxing.model.dto.request;

import java.math.BigDecimal;

// Partial update: null olan sahələr dəyişdirilmir.
public record UpdateTaskRequest(
        String title,
        String description,
        BigDecimal budget
) { }
