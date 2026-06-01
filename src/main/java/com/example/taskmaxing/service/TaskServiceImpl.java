package com.example.taskmaxing.service;

import com.example.taskmaxing.GlobalErroring.ConflictException;
import com.example.taskmaxing.GlobalErroring.ForbiddenException;
import com.example.taskmaxing.GlobalErroring.ResourceNotFoundException;
import com.example.taskmaxing.mapper.TaskMapper;
import com.example.taskmaxing.model.dto.request.CreateTaskRequest;
import com.example.taskmaxing.model.dto.request.UpdateTaskRequest;
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

    // İş tam bitdikdə (client təsdiqi) tasker-ə verilən karma. İstənilən vaxt dəyişdirilə bilər.
    private static final long KARMA_REWARD = 10L;

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

    @Transactional
    @Override
    public TaskResponse updateTask(Long taskId, UpdateTaskRequest request, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tapşırıq tapılmadı! ID: " + taskId));

        // Yalnız tapşırığı yaradan (sahibi) onu redaktə edə bilər
        if (!task.getClient().getUsername().equals(username)) {
            throw new ForbiddenException("Yalnız öz tapşırığınızı redaktə edə bilərsiniz!");
        }

        // Bir icraçı götürdükdən sonra tapşırığın şərtlərini dəyişmək olmaz
        if (task.getStatus() != TaskStatus.PENDING) {
            throw new ConflictException("Yalnız hələ götürülməmiş (PENDING) tapşırıqları redaktə etmək olar!");
        }

        // Partial update: yalnız göndərilən (null olmayan) sahələr yenilənir
        if (request.title() != null) {
            task.setTitle(request.title());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.budget() != null) {
            task.setBudget(request.budget());
        }

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    @Override
    public TaskResponse completeTask(Long taskId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tapşırıq tapılmadı! ID: " + taskId));

        // Yalnız tapşırığı icra edən (tasker) "tamamladım" deyə bilər
        if (task.getTasker() == null || !task.getTasker().getUsername().equals(username)) {
            throw new ForbiddenException("Yalnız tapşırığı icra edən (tasker) onu tamamlaya bilər!");
        }

        // Yalnız icrada olan iş tamamlana bilər
        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new ConflictException("Yalnız icrada (IN_PROGRESS) olan tapşırığı tamamlamaq olar!");
        }

        // Karma hələ verilmir — client təsdiqini gözləyirik
        task.setStatus(TaskStatus.COMPLETED);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    @Override
    public TaskResponse confirmTask(Long taskId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tapşırıq tapılmadı! ID: " + taskId));

        // Yalnız tapşırığı yaradan (client) işin bitdiyini təsdiq edə bilər
        if (!task.getClient().getUsername().equals(username)) {
            throw new ForbiddenException("Yalnız tapşırığı yaradan (client) onu təsdiq edə bilər!");
        }

        // Yalnız tasker-in tamamladığı (COMPLETED) iş təsdiqlənə bilər
        if (task.getStatus() != TaskStatus.COMPLETED) {
            throw new ConflictException("Yalnız tasker tərəfindən tamamlanmış (COMPLETED) tapşırığı təsdiq etmək olar!");
        }

        // İş bitdi → tasker karma qazanır
        task.setStatus(TaskStatus.DONE);

        User tasker = task.getTasker();
        long current = tasker.getKarmaPoints() == null ? 0L : tasker.getKarmaPoints();
        tasker.setKarmaPoints(current + KARMA_REWARD);
        userRepository.save(tasker);

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