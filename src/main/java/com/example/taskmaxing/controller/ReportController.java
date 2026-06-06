package com.example.taskmaxing.controller;

import com.example.taskmaxing.model.dto.request.CreateReportRequest;
import com.example.taskmaxing.model.dto.response.ReportResponse;
import com.example.taskmaxing.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // İstənilən login olmuş istifadəçi başqa bir istifadəçini report edir.
    @PostMapping
    public ResponseEntity<ReportResponse> create(
            @Valid @RequestBody CreateReportRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(reportService.createReport(authentication.getName(), request));
    }
}
