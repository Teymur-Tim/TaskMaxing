package com.example.taskmaxing.controller;

import com.example.taskmaxing.model.dto.response.AdminUserResponse;
import com.example.taskmaxing.model.dto.response.ReportResponse;
import com.example.taskmaxing.model.enums.ReportStatus;
import com.example.taskmaxing.service.AdminService;
import com.example.taskmaxing.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ReportService reportService;

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> users() {
        return ResponseEntity.ok(adminService.listUsers());
    }

    @PutMapping("/users/{username}/ban")
    public ResponseEntity<AdminUserResponse> ban(@PathVariable String username) {
        return ResponseEntity.ok(adminService.banUser(username));
    }

    @PutMapping("/users/{username}/unban")
    public ResponseEntity<AdminUserResponse> unban(@PathVariable String username) {
        return ResponseEntity.ok(adminService.unbanUser(username));
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<Void> delete(@PathVariable String username) {
        adminService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reports")
    public ResponseEntity<List<ReportResponse>> reports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @PutMapping("/reports/{id}/status")
    public ResponseEntity<ReportResponse> updateReportStatus(
            @PathVariable Long id,
            @RequestParam ReportStatus status
    ) {
        return ResponseEntity.ok(reportService.updateStatus(id, status));
    }
}
