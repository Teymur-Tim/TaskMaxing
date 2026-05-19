package com.example.taskmaxing.service;

import com.example.taskmaxing.model.dto.request.CreateTaskRequest;
import com.example.taskmaxing.model.dto.response.TaskResponse;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(CreateTaskRequest request, Long clientId);


    List<TaskResponse> getAllTasks();
}
