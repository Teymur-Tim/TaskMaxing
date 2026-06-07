package com.example.taskmaxing.service;

import com.example.taskmaxing.model.dto.request.CreateTaskRequest;
import com.example.taskmaxing.model.dto.request.UpdateTaskRequest;
import com.example.taskmaxing.model.dto.response.NearbyTaskResponse;
import com.example.taskmaxing.model.dto.response.TaskResponse;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(CreateTaskRequest request, String username);

    List<TaskResponse> getAllOpenTasks();
    List<TaskResponse> getAllTasks();

    TaskResponse assignTask(Long taskId, String username);

    TaskResponse updateTask(Long taskId, UpdateTaskRequest request, String username);

    TaskResponse completeTask(Long taskId, String username);

    TaskResponse confirmTask(Long taskId, String username);

    TaskResponse cancelTask(Long taskId, String username);

    void deleteTask(Long taskId, String username);

    void deleteStaleCompletedTasks();

    List<NearbyTaskResponse> getNearbyOpenTasks(double latitude, double longitude, double radiusKm);
}
