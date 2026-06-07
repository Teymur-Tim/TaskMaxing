package com.example.taskmaxing.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReviewRequest(
        @NotNull(message = "Reytinq mütləqdir!")
        @Min(value = 1, message = "Reytinq ən az 1 olmalıdır!")
        @Max(value = 5, message = "Reytinq ən çox 5 olmalıdır!")
        Integer rating,

        @Size(max = 500, message = "Mesaj 500 simvoldan çox ola bilməz!")
        String comment
) { }
