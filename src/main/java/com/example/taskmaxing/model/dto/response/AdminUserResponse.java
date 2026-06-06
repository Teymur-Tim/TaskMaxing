package com.example.taskmaxing.model.dto.response;

import com.example.taskmaxing.model.enums.Role;

import java.util.Set;

// Admin paneli üçün istifadəçi məlumatı — ictimai UserResponse-dan fərqli olaraq
// burada ban statusu və rollar da var (yalnız adminə göstərilir).
public record AdminUserResponse(
        Long id,
        String username,
        String email,
        String phoneNumber,
        boolean banned,
        Set<Role> roles,
        Long karmaPoints,
        Double ratingAverage,
        Long ratingCount,
        String avatar
) { }
