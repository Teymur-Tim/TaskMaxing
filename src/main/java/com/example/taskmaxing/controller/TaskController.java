package com.example.taskmaxing.controller;

import com.example.taskmaxing.model.dto.request.CreateTaskRequest;
import com.example.taskmaxing.model.dto.response.TaskResponse;
import com.example.taskmaxing.model.entity.User;
import com.example.taskmaxing.repository.UserRepository;
import com.example.taskmaxing.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


    @RestController
    @RequestMapping("/tasks") //
    @RequiredArgsConstructor
    public class TaskController {
        private final TaskService taskService;

        @PostMapping("/create/{clientId}")
        public ResponseEntity<TaskResponse> createTask(@PathVariable Long clientId,
                                                       @RequestBody CreateTaskRequest request) {
            TaskResponse response = taskService.createTask(request,clientId);
            return ResponseEntity.ok(response);
        }

    }

