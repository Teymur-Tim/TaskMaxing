package com.example.taskmaxing.mapper;

import com.example.taskmaxing.model.dto.response.ReviewResponse;
import com.example.taskmaxing.model.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "reviewer.username", target = "reviewerName")
    @Mapping(source = "receiver.username", target = "receiverName")
    @Mapping(source = "task.id", target = "taskId")
    ReviewResponse toResponse(Review review);
}
