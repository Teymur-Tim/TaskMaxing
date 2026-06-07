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

    long countByTaskerAndStatusIn(User tasker, List<TaskStatus> statuses);

    List<Task> findByTaskerIsNull();


    List<Task> findByClientIdAndTaskerIsNotNull(Long clientId);

    List<Task> findByTaskerId(Long taskerId);

    List<Task> findByStatusAndCompletedAtBefore(TaskStatus status, Instant cutoff);
}