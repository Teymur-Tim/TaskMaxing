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

    // Tasker "tamamladım" dediyi an. Client 1 gün ərzində təsdiqləməsə,
    // planlaşdırılmış iş bu tapşırığı avtomatik silir (yeri boş yerə tutmasın).
    private Instant completedAt;

    // GPS məlumatı (xəritə kitabxanası yox, sadəcə saxlanılır və Google Maps linki üçün istifadə olunur)
    private Double latitude;
    private Double longitude;
    private String address;

    // EAGER olmalıdır: User @SoftDelete olduğu üçün Hibernate to-one əlaqəni LAZY saxlaya bilmir
    // (silinib-silinmədiyini bilmək üçün entity-ni yükləməlidir).
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tasker_id")
    private User tasker; // Bu işi qəbul edən / icra edən şəxs

}