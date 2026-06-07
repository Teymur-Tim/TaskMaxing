package com.example.taskmaxing.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(
        name = "messages",
        indexes = @Index(name = "idx_messages_task_id", columnList = "task_id")
)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Instant createdAt;
}
