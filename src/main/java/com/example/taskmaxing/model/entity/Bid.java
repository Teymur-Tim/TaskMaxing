package com.example.taskmaxing.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
//istifade olunmurr!!!
@Entity
@Data
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;
    private String proposalText;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "tasker_id")
    private User tasker;
}