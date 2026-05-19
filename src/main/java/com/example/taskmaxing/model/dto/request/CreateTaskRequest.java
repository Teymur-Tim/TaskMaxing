package com.example.taskmaxing.model.dto.request;

import java.math.BigDecimal;

public record CreateTaskRequest(
        String title,
        String description,
        BigDecimal budget
) { }
