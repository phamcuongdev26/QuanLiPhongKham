package com.clinic.service;

import com.clinic.dto.response.AuditLogResponse;
import com.clinic.dto.response.PageResponse;

import java.time.LocalDate;

public interface AuditLogService {

    void log(String action, String entityType, Long entityId, String entityName,
             String adminUsername, String ipAddress, Object oldValue, Object newValue, String detail);

    PageResponse<AuditLogResponse> getPage(int page, int size,
                                           String entityType, String action,
                                           LocalDate from, LocalDate to);
}
