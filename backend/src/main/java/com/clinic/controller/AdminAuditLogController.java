package com.clinic.controller;

import com.clinic.dto.response.ApiResponse;
import com.clinic.dto.response.AuditLogResponse;
import com.clinic.dto.response.PageResponse;
import com.clinic.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
public class
AdminAuditLogController {
    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> getLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        PageResponse<AuditLogResponse> result = auditLogService.getPage(page, size, entityType, action, from, to);
        return ResponseEntity.ok(ApiResponse.<PageResponse<AuditLogResponse>>builder()
                .code(200)
                .result(result)
                .build());
    }
}
