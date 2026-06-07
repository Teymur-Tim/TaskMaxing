package com.example.taskmaxing.model.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record UpdateTaskRequest(
        String title,
        String description,
        BigDecimal budget,
        @DecimalMin(value = "-90.0", message = "latitude -90 ilə 90 arası olmalıdır!")
        @DecimalMax(value = "90.0", message = "latitude -90 ilə 90 arası olmalıdır!")
        Double latitude,
        @DecimalMin(value = "-180.0", message = "longitude -180 ilə 180 arası olmalıdır!")
        @DecimalMax(value = "180.0", message = "longitude -180 ilə 180 arası olmalıdır!")
        Double longitude,
        String address
) { }
