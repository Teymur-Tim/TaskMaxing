package com.example.taskmaxing.GlobalErroring;

// 404 - axtarılan resurs (user, task, token) bazada tapılmadıqda atılır
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
