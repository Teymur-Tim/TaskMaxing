package com.example.taskmaxing.controller;

import com.example.taskmaxing.model.dto.request.CreateTaskRequest;
import com.example.taskmaxing.model.dto.request.UpdateTaskRequest;
import com.example.taskmaxing.model.dto.response.NearbyTaskResponse;
import com.example.taskmaxing.model.dto.response.TaskResponse;
import com.example.taskmaxing.model.entity.User;
import com.example.taskmaxing.repository.UserRepository;
import com.example.taskmaxing.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/tasks") //
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/create") // URL-dən /{clientId} hissəsini tamamilə sildik!
    public ResponseEntity<TaskResponse> createTask(
            @RequestBody CreateTaskRequest request,
            Authentication authentication // Sistemdə olan aktiv istifadəçini avtomatik tutmaq üçün
    ) {
        // Token-in içindən qeydiyyatdan keçmiş istifadəçinin username-ni çıxarırıq
        String currentUsername = authentication.getName();

        // Servisə həmin username-i ötürürük
        TaskResponse response = taskService.createTask(request, currentUsername);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/open-tasks")
//        @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TaskResponse>> getAllOpenTasks() {
        return ResponseEntity.ok(taskService.getAllOpenTasks());
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<TaskResponse> assignTask(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String currentUsername = authentication.getName();
        TaskResponse response = taskService.assignTask(id, currentUsername);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/alltasks")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    // Tapşırığı redaktə et: yalnız sahibi və yalnız PENDING statusunda olduqda
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request,
            Authentication authentication
    ) {
        String currentUsername = authentication.getName();
        TaskResponse response = taskService.updateTask(id, request, currentUsername);
        return ResponseEntity.ok(response);
    }

    // Tasker işi bitirdiyini bildirir → status COMPLETED (client təsdiqini gözləyir)
    @PutMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(taskService.completeTask(id, authentication.getName()));
    }

    // Client işi təsdiqləyir → status DONE + tasker karma qazanır
    @PutMapping("/{id}/confirm")
    public ResponseEntity<TaskResponse> confirmTask(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(taskService.confirmTask(id, authentication.getName()));
    }

    // Yaxınlıqdakı açıq tapşırıqlar: tasker öz yerini (lat,lng) verir, radius (km) opsionaldır
    @GetMapping("/nearby")
    public ResponseEntity<List<NearbyTaskResponse>> nearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10") double radiusKm
    ) {
        return ResponseEntity.ok(taskService.getNearbyOpenTasks(lat, lng, radiusKm));
    }

}

