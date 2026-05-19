package com.example.taskmaxing.model.dto.response;

public record UserResponse(
        Long id,
        String username,
        String email,
        String bio,
        Long karmaPoints
) {}