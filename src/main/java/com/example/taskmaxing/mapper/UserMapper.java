package com.example.taskmaxing.mapper;

import com.example.taskmaxing.model.dto.request.CreateUserRequest;
import com.example.taskmaxing.model.dto.response.UserResponse;
import com.example.taskmaxing.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(CreateUserRequest request);

    @Mapping(target = "ratingAverage", expression = "java(ratingAverage(user))")
    UserResponse toResponse(User user);

    // Orta reytinq: ratingSum / ratingCount, bir onluğa yuvarlaqlaşdırılır.
    // Heç bir rəy yoxdursa null (UI "Hələ rəy yoxdur" göstərə bilsin).
    default Double ratingAverage(User user) {
        long count = user.getRatingCount() == null ? 0L : user.getRatingCount();
        long sum = user.getRatingSum() == null ? 0L : user.getRatingSum();
        return count > 0 ? Math.round((double) sum / count * 10.0) / 10.0 : null;
    }
}
