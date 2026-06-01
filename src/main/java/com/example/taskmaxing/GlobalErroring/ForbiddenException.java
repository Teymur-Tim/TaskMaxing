package com.example.taskmaxing.GlobalErroring;

// 403 - istifadəçi autentifikasiya olunub, amma bu əməliyyata icazəsi yoxdur
// (məs: başqasının tapşırığını redaktə etmək)
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
