package com.example.taskmaxing.mapper;

import com.example.taskmaxing.model.dto.response.ReportResponse;
import com.example.taskmaxing.model.entity.Report;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ReportMapper {
    ReportResponse toResponse(Report report);
}
