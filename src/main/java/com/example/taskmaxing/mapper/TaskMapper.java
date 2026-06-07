package com.example.taskmaxing.mapper;

import com.example.taskmaxing.model.dto.request.CreateTaskRequest;
import com.example.taskmaxing.model.dto.response.TaskResponse;
import com.example.taskmaxing.model.entity.Task;
import com.example.taskmaxing.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(source = "client.username", target = "clientName")
    @Mapping(source = "tasker.username", target = "taskerName")
    @Mapping(target = "clientRating", expression = "java(ratingAverage(task.getClient()))")
    @Mapping(target = "clientRatingCount", expression = "java(ratingCount(task.getClient()))")
    @Mapping(target = "mapsUrl", expression = "java(buildMapsUrl(task))")
    TaskResponse toResponse(Task task);

    Task toEntity(CreateTaskRequest request);


    default Double ratingAverage(User user) {
        if (user == null) return null;
        long count = user.getRatingCount() == null ? 0L : user.getRatingCount();
        long sum = user.getRatingSum() == null ? 0L : user.getRatingSum();
        return count > 0 ? Math.round((double) sum / count * 10.0) / 10.0 : null;
    }

    default Long ratingCount(User user) {
        if (user == null || user.getRatingCount() == null) return 0L;
        return user.getRatingCount();
    }

    default String buildMapsUrl(Task task) {
        if (task.getLatitude() != null && task.getLongitude() != null) {
            return "https://www.google.com/maps/search/?api=1&query="
                    + task.getLatitude() + "," + task.getLongitude();
        }
        if (task.getAddress() != null && !task.getAddress().isBlank()) {
            return "https://www.google.com/maps/search/?api=1&query="
                    + URLEncoder.encode(task.getAddress(), StandardCharsets.UTF_8);
        }
        return null;
    }
}
