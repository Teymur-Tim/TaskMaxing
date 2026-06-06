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

    // Söhbətin ilk yüklənməsi: tapşırığın bütün mesajları, köhnədən yeniyə.
    List<Message> findByTaskIdOrderByIdAsc(Long taskId);

    // Delta-polling: yalnız müştərinin gördüyü son mesajdan SONRAKILAR (id > afterId).
    // Əksər sorğularda boş qayıdır — indeksli, çox ucuz.
    List<Message> findByTaskIdAndIdGreaterThanOrderByIdAsc(Long taskId, Long afterId);

    // Söhbətlər siyahısı üçün tapşırığın ən son mesajı (yoxdursa boş).
    Optional<Message> findTopByTaskIdOrderByIdDesc(Long taskId);

    // Köhnə bitmiş tapşırıqların təmizlənməsi: status DONE və doneAt verilən andan
    // əvvəl olan tapşırıqların bütün mesajlarını silir. Silinən mesaj sayını qaytarır.
    long deleteByTask_StatusAndTask_DoneAtBefore(TaskStatus status, Instant cutoff);
}
