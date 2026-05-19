package com.example.taskmaxing.model.dto.response;

import java.math.BigDecimal;

public record TaskResponse(Long id,
                           String title,
                           String description,
                           BigDecimal budget,
                           String status,
                           String clientName) {
}
