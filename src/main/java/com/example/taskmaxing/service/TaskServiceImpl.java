package com.example.taskmaxing.service;

import com.example.taskmaxing.GlobalErroring.ConflictException;
import com.example.taskmaxing.GlobalErroring.ResourceNotFoundException;
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
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı!"));

        // Biznes Qaydası Yoxlanışı (Eyni anda həm Tasker həm Client olmamaq şərti)
        // Əgər bu user-in hal-hazırda icra etdiyi (Tasker olduğu) aktiv bir işi varsa, yeni task yarada bilməz
        boolean isAlreadyTasker = taskRepository.existsByTaskerAndStatusIn(user, List.of(TaskStatus.IN_PROGRESS));
        if (isAlreadyTasker) {
            throw new ConflictException("Aktiv icra etdiyiniz iş var! Bitmədən yeni task yarada bilməzsiniz.");
        }

        // 2. Taskı yaradırıq və useri ona bağlayırıq
        Task task = taskMapper.toEntity(request);

        task.setClient(user);
        task.setStatus(TaskStatus.PENDING); // Bayaq düzəltdiyimiz default status

        return taskMapper.toResponse(taskRepository.save(task));
    }
    @Transactional(readOnly = true)
    @Override
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toResponse) // Hər bir task-ı DTO-ya çevirir
                .toList();
    }
    @Transactional
    @Override
    public TaskResponse assignTask(Long taskId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tapşırıq tapılmadı! ID: " + taskId));

        User tasker = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı: " + username));

        if (task.getTasker() != null) {
            throw new ConflictException("Bu tapşırıq artıq götürülüb!");
        }

        if (task.getClient().getId().equals(tasker.getId())) {
            throw new ConflictException("Öz yaratdığınız tapşırığı icraçı kimi qəbul edə bilməzsiniz!");
        }

        task.setTasker(tasker);
        task.setStatus(TaskStatus.IN_PROGRESS);

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    @Override
    public List<TaskResponse> getAllOpenTasks() {
        // 1. Bazadan icraçısı olmayan taskları çəkirik
        List<Task> openTasks = taskRepository.findByTaskerIsNull();

        // 2. Stream və mapper istifadə edərək Entity-ləri Response DTO-ya çeviririk
        return openTasks.stream()
                .map(taskMapper::toResponse)
                .toList();
    }


}