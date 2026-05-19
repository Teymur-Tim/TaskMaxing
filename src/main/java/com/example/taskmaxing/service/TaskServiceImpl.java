package com.example.taskmaxing.service;

import com.example.taskmaxing.mapper.TaskMapper;
import com.example.taskmaxing.model.dto.request.CreateTaskRequest;
import com.example.taskmaxing.model.dto.response.TaskResponse;
import com.example.taskmaxing.model.entity.Task;
import com.example.taskmaxing.model.entity.User;
import com.example.taskmaxing.model.enums.TaskStatus;
import com.example.taskmaxing.repository.TaskRepository;
import com.example.taskmaxing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // Lombok: Constructor injection üçün
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponse createTask(CreateTaskRequest request, Long clientId) {
        // 1. Bazadan həmin ID-li useri tapırıq
        User user = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı!"));
        // 2. Taskı yaradırıq və useri ona bağlayırıq
        Task task = taskMapper.toEntity(request);

        task.setClient(user); // Bax bura əsas hissədir
        task.setStatus(TaskStatus.PENDING);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toResponse) // Hər bir task-ı DTO-ya çevirir
                .toList();
    }
}