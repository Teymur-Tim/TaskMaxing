package com.example.taskmaxing.mapper;

import com.example.taskmaxing.model.dto.request.CreateTaskRequest;
import com.example.taskmaxing.model.dto.response.TaskResponse;
import com.example.taskmaxing.model.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(source = "client.username", target = "clientName")
    TaskResponse toResponse(Task task);

    Task toEntity(CreateTaskRequest request);
}

//unmappedTargetPolicy = ReportingPolicy.IGNORE