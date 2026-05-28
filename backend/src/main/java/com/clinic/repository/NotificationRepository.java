package com.clinic.repository;

import com.clinic.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query(value = "SELECT * FROM notifications WHERE user_id = :userId ORDER BY created_at DESC", nativeQuery = true)
    List<Notification> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query(value = "SELECT COUNT(*) FROM notifications WHERE user_id = :userId AND is_read = false", nativeQuery = true)
    long countUnreadByUserId(@Param("userId") Long userId);

    @Modifying
    @Query(value = "UPDATE notifications SET is_read = true WHERE user_id = :userId", nativeQuery = true)
    void markAllReadByUserId(@Param("userId") Long userId);
}
