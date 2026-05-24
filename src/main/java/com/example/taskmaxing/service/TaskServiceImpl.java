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
    public TaskResponse createTask(CreateTaskRequest request, String username) {
        // 1. Bazadan ID ilə yox, token-dən gələn username ilə user-i tapırıq
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı!"));

        // Biznes Qaydası Yoxlanışı (Eyni anda həm Tasker həm Client olmamaq şərti)
        // Əgər bu user-in hal-hazırda icra etdiyi (Tasker olduğu) aktiv bir işi varsa, yeni task yarada bilməz
        boolean isAlreadyTasker = taskRepository.existsByTaskerAndStatusIn(user, List.of(TaskStatus.IN_PROGRESS));
        if (isAlreadyTasker) {
            throw new RuntimeException("Aktiv icra etdiyiniz iş var! Bitmədən yeni task yarada bilməzsiniz.");
        }

        // 2. Taskı yaradırıq və useri ona bağlayırıq
        Task task = taskMapper.toEntity(request);

        task.setClient(user);
        task.setStatus(TaskStatus.PENDING); // Bayaq düzəltdiyimiz default status

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toResponse) // Hər bir task-ı DTO-ya çevirir
                .toList();
    }
}