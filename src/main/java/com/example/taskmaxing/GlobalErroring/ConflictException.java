package com.example.taskmaxing.GlobalErroring;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
