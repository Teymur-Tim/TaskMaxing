package com.example.taskmaxing.GlobalErroring;

// 409 - biznes qaydası pozulduqda (təkrar istifadəçi, artıq götürülmüş task və s.) atılır
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
