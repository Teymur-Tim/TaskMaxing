package com.example.taskmaxing.model.dto.response;

public record NearbyTaskResponse(
        TaskResponse task,
        double distanceKm
) { }
