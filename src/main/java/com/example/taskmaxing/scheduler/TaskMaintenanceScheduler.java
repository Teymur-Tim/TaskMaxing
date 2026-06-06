package com.example.taskmaxing.scheduler;

import com.example.taskmaxing.service.MessageService;
import com.example.taskmaxing.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// Arxa planda işləyən təmizləyici: köhnə təsdiqlənməmiş COMPLETED taskları və
// çoxdan bitmiş (DONE) tapşırıqların çat mesajlarını müntəzəm silir.
@Component
@RequiredArgsConstructor
public class TaskMaintenanceScheduler {

    private final TaskService taskService;
    private final MessageService messageService;

    // Hər saatda bir dəfə işə düşür (tətbiq başlayandan 1 dəqiqə sonra ilk dəfə).
    @Scheduled(fixedRate = 3_600_000L, initialDelay = 60_000L)
    public void purgeStaleCompletedTasks() {
        taskService.deleteStaleCompletedTasks();
    }

    // Bir neçə gün əvvəl bitmiş tapşırıqların çat mesajlarını silir (yer tutmasın).
    // Tapşırığın özü tarixçə kimi qalır — yalnız mesajlar gedir.
    @Scheduled(fixedRate = 3_600_000L, initialDelay = 120_000L)
    public void purgeOldChatMessages() {
        messageService.purgeMessagesOfOldDoneTasks();
    }
}
