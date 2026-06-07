package com.example.taskmaxing.model.dto.response;

public record UserResponse(
        Long id,
        String username,
        String email,
        String phoneNumber,
        Boolean phoneVisible,
        String bio,
        Long karmaPoints,
        String avatar,
        Double ratingAverage,
        Long ratingCount
) {}