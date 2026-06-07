package com.example.taskmaxing.repository;

import com.example.taskmaxing.model.entity.Message;
import com.example.taskmaxing.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {


    List<Message> findByTaskIdOrderByIdAsc(Long taskId);


    List<Message> findByTaskIdAndIdGreaterThanOrderByIdAsc(Long taskId, Long afterId);


    Optional<Message> findTopByTaskIdOrderByIdDesc(Long taskId);

    long deleteByTask_StatusAndTask_DoneAtBefore(TaskStatus status, Instant cutoff);
}
