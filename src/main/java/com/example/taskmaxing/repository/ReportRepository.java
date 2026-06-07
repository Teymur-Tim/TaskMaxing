package com.example.taskmaxing.repository;

import com.example.taskmaxing.model.entity.Report;
import com.example.taskmaxing.model.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByOrderByCreatedAtDesc();

    List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status);
}
