package com.example.taskmaxing.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(
        @NotBlank(message = "Mesaj boş ola bilməz!")
        @Size(max = 2000, message = "Mesaj 2000 simvoldan çox ola bilməz!")
        String content
) { }
