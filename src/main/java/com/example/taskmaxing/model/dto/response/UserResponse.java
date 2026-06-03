package com.example.taskmaxing.model.dto.response;

public record UserResponse(
        Long id,
        String username,
        String email,
        // Telefon nömrəsi: öz profilində həmişə, ictimai profildə yalnız phoneVisible=true olduqda.
        String phoneNumber,
        // Nömrənin ictimai görünürlüyü (UI gizlət/göstər düyməsinin vəziyyəti üçün).
        Boolean phoneVisible,
        String bio,
        Long karmaPoints,
        String avatar,
        // Orta reytinq (1–5, bir onluğa yuvarlaqlaşdırılmış) və neçə rəyə əsaslanır
        Double ratingAverage,
        Long ratingCount
) {}