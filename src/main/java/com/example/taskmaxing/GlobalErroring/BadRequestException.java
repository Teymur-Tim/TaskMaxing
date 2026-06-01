package com.example.taskmaxing.GlobalErroring;

// 400 - client səhv məlumat göndərdikdə (məs: cari şifrə yanlışdır) atılır
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
