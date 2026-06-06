package com.example.taskmaxing.model.enums;

// Şikayətin (report) həyat dövrü.
public enum ReportStatus {
    PENDING,    // Yeni gəlib — admin hələ baxmayıb.
    RESOLVED,   // Admin tədbir gördü (ban/sil) və ya əsaslı saydı.
    DISMISSED;  // Admin əsassız sayıb rədd etdi.
}
