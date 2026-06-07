package com.example.taskmaxing.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "review", uniqueConstraints = @UniqueConstraint(
        name = "uq_review_task_reviewer", columnNames = {"task_id", "reviewer_id"}))
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rating;

    @Column(length = 500)
    private String comment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id")
    private Task task;

    private Instant createdAt;
}