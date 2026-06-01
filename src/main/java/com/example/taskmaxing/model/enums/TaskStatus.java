package com.example.taskmaxing.model.enums;

public enum TaskStatus {
    PENDING,        // yaradılıb, hələ icraçı yoxdur
    ASSIGNED,
    IN_PROGRESS,    // icraçı götürüb, işləyir
    COMPLETED,      // tasker "tamamladım" deyib, client təsdiqini gözləyir
    DONE,           // client təsdiqləyib — iş bitdi, tasker karma qazandı
    CANCELLED
}