package com.example.taskmaxing.GlobalErroring;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
