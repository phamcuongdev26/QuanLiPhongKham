package com.clinic.repository;

import com.clinic.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_IdOrderByCreatedAtDesc(Long userId);
    long countByUser_IdAndIsReadFalse(Long userId);

    @Modifying
    @Query(value = "UPDATE notifications SET is_read = true WHERE user_id = :userId", nativeQuery = true)
    void markAllReadByUserId(@Param("userId") Long userId);
}
