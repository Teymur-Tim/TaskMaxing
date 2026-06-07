package com.example.taskmaxing.model.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record TaskResponse(Long id,
                           String title,
                           String description,
                           BigDecimal budget,
                           String status,
                           String clientName,
                           String taskerName,
                           Double latitude,
                           Double longitude,
                           String address,
                           Instant completedAt,
                           Double clientRating,
                           Long clientRatingCount,
                           String mapsUrl) {
}
