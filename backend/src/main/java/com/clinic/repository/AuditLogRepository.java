package com.clinic.repository;

import com.clinic.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query(value = """
        SELECT * FROM audit_logs
        WHERE (:entityType IS NULL OR entity_type = :entityType)
          AND (:action IS NULL OR action = :action)
          AND (:from IS NULL OR created_at >= :from)
          AND (:to IS NULL OR created_at <= :to)
        ORDER BY created_at DESC
    """, countQuery = """
        SELECT COUNT(*) FROM audit_logs
        WHERE (:entityType IS NULL OR entity_type = :entityType)
          AND (:action IS NULL OR action = :action)
          AND (:from IS NULL OR created_at >= :from)
          AND (:to IS NULL OR created_at <= :to)
    """, nativeQuery = true)
    Page<AuditLog> findFiltered(
            @Param("entityType") String entityType,
            @Param("action") String action,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );
}
