package com.clinic.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {
    private Long id;
    private String action;
    private String entityType;
    private Long entityId;
    private String entityName;
    private String adminUsername;
    private String adminFullName;
    private String oldValue;
    private String newValue;
    private String detail;
    private LocalDateTime createdAt;
}
