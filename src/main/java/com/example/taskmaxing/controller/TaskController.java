package com.example.taskmaxing.controller;

import com.example.taskmaxing.model.dto.request.CreateTaskRequest;
import com.example.taskmaxing.model.dto.response.TaskResponse;
import com.example.taskmaxing.model.entity.User;
import com.example.taskmaxing.repository.UserRepository;
import com.example.taskmaxing.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


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

    }

