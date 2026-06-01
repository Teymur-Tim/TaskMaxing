package com.example.taskmaxing.model.dto.response;

// Tapşırıq + tasker-in verdiyi nöqtədən neçə km uzaqda olduğu
public record NearbyTaskResponse(
        TaskResponse task,
        double distanceKm
) { }
