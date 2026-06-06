package com.example.taskmaxing.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

// Tapşırıq daxili çat mesajı. Söhbət ayrıca cədvəl deyil — hər mesaj birbaşa
// Task-a bağlıdır, iştirakçılar isə həmin Task-ın client-i və tasker-idir.
// "Söhbəti yükləmək" = task_id-yə görə bütün mesajları sıra ilə gətirmək.
@Entity
@Data
@Table(
        name = "messages",
        // Delta-polling sorğusu (WHERE task_id = ? AND id > ?) bu indekslə ucuz olur.
        indexes = @Index(name = "idx_messages_task_id", columnList = "task_id")
)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mesajın aid olduğu tapşırıq. Task @SoftDelete deyil, ona görə LAZY saxlana bilər.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id")
    private Task task;

    // EAGER olmalıdır: User @SoftDelete olduğu üçün Hibernate to-one əlaqəni LAZY saxlaya bilmir.
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sender_id")
    private User sender;

    // Mesaj mətni — uzun ola bilər deyə TEXT (Postgres-də @Lob/OID problemlərindən qaçmaq üçün).
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Instant createdAt;
}
