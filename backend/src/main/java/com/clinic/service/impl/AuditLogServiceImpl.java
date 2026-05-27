package com.clinic.service.impl;

import com.clinic.dto.response.AuditLogResponse;
import com.clinic.dto.response.PageResponse;
import com.clinic.entity.AuditLog;
import com.clinic.entity.User;
import com.clinic.repository.AuditLogRepository;
import com.clinic.repository.UserRepository;
import com.clinic.service.AuditLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void log(String action, String entityType, Long entityId, String entityName,
                    String adminUsername, Object oldValue, Object newValue, String detail) {
        try {
            String adminFullName = userRepository.findByUsername(adminUsername)
                    .map(User::getFullName).orElse(adminUsername);

            AuditLog entry = AuditLog.builder()
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .entityName(entityName)
                    .adminUsername(adminUsername)
                    .adminFullName(adminFullName)
                    .oldValue(oldValue != null ? objectMapper.writeValueAsString(oldValue) : null)
                    .newValue(newValue != null ? objectMapper.writeValueAsString(newValue) : null)
                    .detail(detail)
                    .build();

            auditLogRepository.save(entry);
        } catch (Exception e) {
            log.error("Failed to write audit log: {}", e.getMessage());
        }
    }

    @Override
    public PageResponse<AuditLogResponse> getPage(int page, int size,
                                                   String entityType, String action,
                                                   LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from != null ? from.atStartOfDay() : null;
        LocalDateTime toDt = to != null ? to.atTime(LocalTime.MAX) : null;

        Page<AuditLog> result = auditLogRepository.findFiltered(
                (entityType == null || entityType.isBlank()) ? null : entityType,
                (action == null || action.isBlank()) ? null : action,
                fromDt, toDt,
                PageRequest.of(page, size)
        );

        return PageResponse.<AuditLogResponse>builder()
                .content(result.getContent().stream().map(this::toResponse).toList())
                .page(result.getNumber())
                .totalPages(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .build();
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .entityName(log.getEntityName())
                .adminUsername(log.getAdminUsername())
                .adminFullName(log.getAdminFullName())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .detail(log.getDetail())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
