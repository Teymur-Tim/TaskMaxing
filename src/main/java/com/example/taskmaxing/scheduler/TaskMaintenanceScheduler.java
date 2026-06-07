package com.example.taskmaxing.scheduler;

import com.example.taskmaxing.service.MessageService;
import com.example.taskmaxing.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TaskMaintenanceScheduler {

    private final TaskService taskService;
    private final MessageService messageService;

    @Scheduled(fixedRate = 3_600_000L, initialDelay = 60_000L)
    public void purgeStaleCompletedTasks() {
        taskService.deleteStaleCompletedTasks();
    }

    @Scheduled(fixedRate = 3_600_000L, initialDelay = 120_000L)
    public void purgeOldChatMessages() {
        messageService.purgeMessagesOfOldDoneTasks();
    }
}
