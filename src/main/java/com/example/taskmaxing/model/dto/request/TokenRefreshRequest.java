package com.example.taskmaxing.model.dto.request;

public class TokenRefreshRequest {
    private String refreshToken;
    // getter, setter və ya @Data
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}