package com.example.taskmaxing.service;

import com.example.taskmaxing.GlobalErroring.ConflictException;
import com.example.taskmaxing.GlobalErroring.ForbiddenException;
import com.example.taskmaxing.GlobalErroring.ResourceNotFoundException;
import com.example.taskmaxing.mapper.TaskMapper;
import com.example.taskmaxing.model.dto.request.CreateTaskRequest;
import com.example.taskmaxing.model.dto.request.UpdateTaskRequest;
import com.example.taskmaxing.model.dto.response.NearbyTaskResponse;
import com.example.taskmaxing.model.dto.response.TaskResponse;
import com.example.taskmaxing.model.entity.Task;
import com.example.taskmaxing.model.entity.User;
import com.example.taskmaxing.model.enums.TaskStatus;
import com.example.taskmaxing.repository.TaskRepository;
import com.example.taskmaxing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private static final long KARMA_REWARD = 10L;

    private static final long MAX_ACTIVE_TASKS = 3L;

    private static final Duration COMPLETED_TASK_TTL = Duration.ofDays(1);

    private static final double EARTH_RADIUS_KM = 6371.0;

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponse createTask(CreateTaskRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı!"));

        boolean isAlreadyTasker = taskRepository.existsByTaskerAndStatusIn(user, List.of(TaskStatus.IN_PROGRESS));
        if (isAlreadyTasker) {
            throw new ConflictException("Aktiv icra etdiyiniz iş var! Bitmədən yeni task yarada bilməzsiniz.");
        }

        Task task = taskMapper.toEntity(request);

        task.setClient(user);
        task.setStatus(TaskStatus.PENDING);

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


        long activeTasks = taskRepository.countByTaskerAndStatusIn(
                tasker, List.of(TaskStatus.IN_PROGRESS));
        if (activeTasks >= MAX_ACTIVE_TASKS) {
            throw new ConflictException(
                    "Eyni anda ən çox " + MAX_ACTIVE_TASKS + " tapşırıq götürə bilərsiniz! " +
                    "Əvvəlcə mövcud tapşırıqlarınızı tamamlayın.");
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

        if (!task.getClient().getUsername().equals(username)) {
            throw new ForbiddenException("Yalnız öz tapşırığınızı redaktə edə bilərsiniz!");
        }

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new ConflictException("Yalnız hələ götürülməmiş (PENDING) tapşırıqları redaktə etmək olar!");
        }

        if (request.title() != null) {
            task.setTitle(request.title());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.budget() != null) {
            task.setBudget(request.budget());
        }
        if (request.latitude() != null) {
            task.setLatitude(request.latitude());
        }
        if (request.longitude() != null) {
            task.setLongitude(request.longitude());
        }
        if (request.address() != null) {
            task.setAddress(request.address());
        }

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    @Override
    public TaskResponse completeTask(Long taskId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tapşırıq tapılmadı! ID: " + taskId));

        if (task.getTasker() == null || !task.getTasker().getUsername().equals(username)) {
            throw new ForbiddenException("Yalnız tapşırığı icra edən (tasker) onu tamamlaya bilər!");
        }

        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new ConflictException("Yalnız icrada (IN_PROGRESS) olan tapşırığı tamamlamaq olar!");
        }

        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(Instant.now());
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    @Override
    public TaskResponse confirmTask(Long taskId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tapşırıq tapılmadı! ID: " + taskId));

        if (!task.getClient().getUsername().equals(username)) {
            throw new ForbiddenException("Yalnız tapşırığı yaradan (client) onu təsdiq edə bilər!");
        }

        if (task.getStatus() != TaskStatus.COMPLETED) {
            throw new ConflictException("Yalnız tasker tərəfindən tamamlanmış (COMPLETED) tapşırığı təsdiq etmək olar!");
        }

        task.setStatus(TaskStatus.DONE);
        task.setDoneAt(Instant.now());
        User tasker = task.getTasker();
        long current = tasker.getKarmaPoints() == null ? 0L : tasker.getKarmaPoints();
        tasker.setKarmaPoints(current + KARMA_REWARD);
        userRepository.save(tasker);

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    @Override
    public TaskResponse cancelTask(Long taskId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tapşırıq tapılmadı! ID: " + taskId));

        if (task.getTasker() == null || !task.getTasker().getUsername().equals(username)) {
            throw new ForbiddenException("Yalnız tapşırığı götürən (tasker) ondan imtina edə bilər!");
        }

        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new ConflictException("Yalnız icrada (IN_PROGRESS) olan tapşırıqdan imtina etmək olar!");
        }

        task.setTasker(null);
        task.setStatus(TaskStatus.PENDING);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    @Override
    public void deleteTask(Long taskId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tapşırıq tapılmadı! ID: " + taskId));

        if (!task.getClient().getUsername().equals(username)) {
            throw new ForbiddenException("Yalnız öz tapşırığınızı silə bilərsiniz!");
        }

        if (task.getStatus() != TaskStatus.PENDING && task.getStatus() != TaskStatus.CANCELLED) {
            throw new ConflictException(
                    "Yalnız hələ icraya başlanmamış (PENDING) və ya ləğv edilmiş tapşırığı silmək olar! " +
                    "İcrada olan iş üçün icraçı imtina etməli, bitmiş iş isə tarixçə kimi qalır.");
        }

        taskRepository.delete(task);
    }

    @Transactional
    @Override
    public void deleteStaleCompletedTasks() {
        Instant cutoff = Instant.now().minus(COMPLETED_TASK_TTL);
        List<Task> stale = taskRepository.findByStatusAndCompletedAtBefore(TaskStatus.COMPLETED, cutoff);
        if (!stale.isEmpty()) {
            taskRepository.deleteAll(stale);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<TaskResponse> getAllOpenTasks() {
        List<Task> openTasks = taskRepository.findByTaskerIsNull();

        return openTasks.stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<NearbyTaskResponse> getNearbyOpenTasks(double latitude, double longitude, double radiusKm) {
        return taskRepository.findByTaskerIsNull().stream()
                .filter(t -> t.getLatitude() != null && t.getLongitude() != null)
                .map(t -> {
                    double distance = haversineKm(latitude, longitude, t.getLatitude(), t.getLongitude());
                    double rounded = Math.round(distance * 100.0) / 100.0;
                    return new NearbyTaskResponse(taskMapper.toResponse(t), rounded);
                })
                .filter(n -> n.distanceKm() <= radiusKm)
                .sorted(Comparator.comparingDouble(NearbyTaskResponse::distanceKm))
                .toList();
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }


}