package com.example.taskmaxing.model.dto.request;

public record CreateUserRequest(
        String username,
        String email,
        String password,
        String bio
) { }
