package com.example.taskmaxing.model.entity;

import com.example.taskmaxing.model.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private BigDecimal budget;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private Instant completedAt;

    private Instant doneAt;

    private Double latitude;
    private Double longitude;
    private String address;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tasker_id")
    private User tasker;

}