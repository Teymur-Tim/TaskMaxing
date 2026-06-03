package com.example.taskmaxing.repository;

import com.example.taskmaxing.model.entity.Task;
import com.example.taskmaxing.model.entity.User;
import com.example.taskmaxing.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);

    List<Task> findByClientId(Long clientId);

    boolean existsByTaskerAndStatusIn(User tasker, List<TaskStatus> statuses);

    // Bir tasker-in eyni anda neçə aktiv (götürdüyü, hələ bitməmiş) işi olduğunu sayır
    long countByTaskerAndStatusIn(User tasker, List<TaskStatus> statuses);

    List<Task> findByTaskerIsNull();

    // Müəyyən statusda olub, verilmiş andan əvvəl o statusa düşmüş tapşırıqlar
    // (köhnə, təsdiqlənməmiş COMPLETED taskları tapmaq üçün).
    List<Task> findByStatusAndCompletedAtBefore(TaskStatus status, Instant cutoff);
}