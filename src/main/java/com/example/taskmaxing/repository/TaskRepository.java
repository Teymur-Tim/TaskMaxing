package com.example.taskmaxing.repository;

import com.example.taskmaxing.model.entity.Task;
import com.example.taskmaxing.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);

    List<Task> findByClientId(Long clientId);
}