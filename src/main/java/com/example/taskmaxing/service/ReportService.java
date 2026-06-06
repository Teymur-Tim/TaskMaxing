package com.example.taskmaxing.service;

import com.example.taskmaxing.GlobalErroring.BadRequestException;
import com.example.taskmaxing.GlobalErroring.ResourceNotFoundException;
import com.example.taskmaxing.mapper.ReportMapper;
import com.example.taskmaxing.model.dto.request.CreateReportRequest;
import com.example.taskmaxing.model.dto.response.ReportResponse;
import com.example.taskmaxing.model.entity.Report;
import com.example.taskmaxing.model.entity.User;
import com.example.taskmaxing.model.enums.ReportStatus;
import com.example.taskmaxing.repository.ReportRepository;
import com.example.taskmaxing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReportMapper reportMapper;

    // İstənilən login olmuş istifadəçi başqasını report edə bilər.
    @Transactional
    public ReportResponse createReport(String reporterUsername, CreateReportRequest request) {
        User reporter = userRepository.findByUsername(reporterUsername)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı!"));
        User reported = userRepository.findByUsername(request.reportedUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Report edilən istifadəçi tapılmadı: " + request.reportedUsername()));

        if (reporter.getId().equals(reported.getId())) {
            throw new BadRequestException("Özünü report edə bilməzsən!");
        }

        Report report = Report.builder()
                .reporter(reporter)
                .reported(reported)
                .reporterUsername(reporter.getUsername())
                .reportedUsername(reported.getUsername())
                .reason(request.reason())
                .status(ReportStatus.PENDING)
                .build();

        return reportMapper.toResponse(reportRepository.save(report));
    }

    // Admin: bütün şikayətlər, ən yenidən köhnəyə.
    @Transactional(readOnly = true)
    public List<ReportResponse> getAllReports() {
        return reportRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(reportMapper::toResponse)
                .toList();
    }

    // Admin: şikayətin statusunu dəyişir (RESOLVED / DISMISSED).
    @Transactional
    public ReportResponse updateStatus(Long id, ReportStatus status) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report tapılmadı: " + id));
        report.setStatus(status);
        return reportMapper.toResponse(reportRepository.save(report));
    }
}
