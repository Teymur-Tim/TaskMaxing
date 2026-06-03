package com.example.taskmaxing.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
// Hər iştirakçı bir tapşırıq üçün yalnız BİR rəy yaza bilər (qarşı tərəf haqqında).
@Table(name = "review", uniqueConstraints = @UniqueConstraint(
        name = "uq_review_task_reviewer", columnNames = {"task_id", "reviewer_id"}))
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1–5 ulduz
    private Integer rating;

    @Column(length = 500)
    private String comment;

    // Rəyi yazan (User @SoftDelete olduğu üçün to-one əlaqələr EAGER olmalıdır)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    // Rəy haqqında olan şəxs (qiymət onun reytinqinə əlavə olunur)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    // Hansı tapşırıqla bağlıdır
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id")
    private Task task;

    private Instant createdAt;
}