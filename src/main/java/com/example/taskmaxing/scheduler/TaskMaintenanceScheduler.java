package com.example.taskmaxing.scheduler;

import com.example.taskmaxing.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// Arxa planda işləyən təmizləyici: client tərəfindən vaxtında təsdiqlənməyən
// köhnə COMPLETED tapşırıqları müntəzəm olaraq silir.
@Component
@RequiredArgsConstructor
public class TaskMaintenanceScheduler {

    private final TaskService taskService;

    // Hər saatda bir dəfə işə düşür (tətbiq başlayandan 1 dəqiqə sonra ilk dəfə).
    @Scheduled(fixedRate = 3_600_000L, initialDelay = 60_000L)
    public void purgeStaleCompletedTasks() {
        taskService.deleteStaleCompletedTasks();
    }
}
