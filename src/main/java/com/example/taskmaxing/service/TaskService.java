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

    // Tasker "tamamladım" deyir → status COMPLETED (client təsdiqini gözləyir)
    TaskResponse completeTask(Long taskId, String username);

    // Client təsdiqləyir → status DONE + tasker karma qazanır
    TaskResponse confirmTask(Long taskId, String username);

    // Verilən nöqtədən radius (km) daxilindəki açıq tapşırıqlar, məsafəyə görə sıralı
    List<NearbyTaskResponse> getNearbyOpenTasks(double latitude, double longitude, double radiusKm);
}
