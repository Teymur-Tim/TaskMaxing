package com.example.taskmaxing.mapper;

import com.example.taskmaxing.model.dto.response.ReportResponse;
import com.example.taskmaxing.model.entity.Report;
import org.mapstruct.Mapper;

// Report -> ReportResponse. Sahə adları üst-üstə düşür (snapshot username-lər),
// ona görə MapStruct avtomatik map edir — reporter/reported lazy entity-yə toxunmuruq.
@Mapper(componentModel = "spring")
public interface ReportMapper {
    ReportResponse toResponse(Report report);
}
