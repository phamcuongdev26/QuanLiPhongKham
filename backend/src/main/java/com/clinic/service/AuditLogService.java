package com.clinic.service;

import com.clinic.dto.response.AuditLogResponse;
import com.clinic.dto.response.ApiResponse;
import com.clinic.dto.response.PageResponse;

public interface AuditLogService {

    void log(String action, String entityType, Long entityId, String entityName,
             String adminUsername, String adminFullName, String detail,
             String oldValue, String newValue);

    ApiResponse<PageResponse<AuditLogResponse>> listLogs(
            int page, int size, String entityType, String action, String from, String to);
}
