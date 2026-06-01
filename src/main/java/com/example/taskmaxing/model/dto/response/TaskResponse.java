package com.example.taskmaxing.model.dto.response;

import java.math.BigDecimal;

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
                           // Tasker düyməyə basanda birbaşa açılacaq Google Maps linki (hazır gəlir)
                           String mapsUrl) {
}
