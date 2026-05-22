package com.clinic.service;

import com.clinic.constant.NotificationType;
import com.clinic.dto.response.NotificationResponse;
import com.clinic.entity.User;

import java.util.List;

public interface NotificationService {
    void send(User user, NotificationType type, String title, String message, Long refAppointmentId);
    List<NotificationResponse> listForUser(String username);
    long countUnread(String username);
    void markRead(String username, Long notificationId);
    void markAllRead(String username);
}
