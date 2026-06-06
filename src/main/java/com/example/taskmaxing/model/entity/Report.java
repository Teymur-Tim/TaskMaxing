package com.example.taskmaxing.model.entity;

import com.example.taskmaxing.model.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

// İstifadəçi şikayəti: kim, kimi, nəyə görə report etdi.
@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Şikayəti edən istifadəçi (stabil əlaqə id-si üçün).
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id")
    private User reporter;

    // Haqqında şikayət edilən istifadəçi.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reported_id")
    private User reported;

    // Username-lərin "snapshot"-u: report anındakı adları saxlayırıq ki,
    // istifadəçi sonradan silinsə/adını dəyişsə belə admin siyahıda kimi
    // görəcəyini itirməsin (soft-delete olunmuş user-i lazy yükləmək problem yaradır).
    @Column(name = "reporter_username", nullable = false)
    private String reporterUsername;

    @Column(name = "reported_username", nullable = false)
    private String reportedUsername;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (status == null) status = ReportStatus.PENDING;
    }
}
