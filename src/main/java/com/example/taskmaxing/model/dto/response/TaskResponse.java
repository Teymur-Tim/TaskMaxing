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
                           // Tasker "tamamladım" dediyi an (təsdiq/expiry vaxtını UI hesablaması üçün)
                           Instant completedAt,
                           // Tapşırığı yaradan client-in reytinqi (tasker götürməzdən əvvəl qiymətləndirsin)
                           Double clientRating,
                           Long clientRatingCount,
                           // Tasker düyməyə basanda birbaşa açılacaq Google Maps linki (hazır gəlir)
                           String mapsUrl) {
}
