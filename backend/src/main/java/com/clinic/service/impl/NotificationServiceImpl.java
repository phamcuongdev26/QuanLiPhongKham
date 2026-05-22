package com.clinic.service.impl;

import com.clinic.constant.NotificationType;
import com.clinic.dto.response.NotificationResponse;
import com.clinic.entity.Notification;
import com.clinic.entity.User;
import com.clinic.exception.AppException;
import com.clinic.exception.ErrorCode;
import com.clinic.repository.NotificationRepository;
import com.clinic.repository.UserRepository;
import com.clinic.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void send(User user, NotificationType type, String title, String message, Long refAppointmentId) {
        notificationRepository.save(Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .refAppointmentId(refAppointmentId)
                .build());
    }

    @Override
    public List<NotificationResponse> listForUser(String username) {
        User user = loadUser(username);
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public long countUnread(String username) {
        User user = loadUser(username);
        return notificationRepository.countByUser_IdAndIsReadFalse(user.getId());
    }

    @Override
    @Transactional
    public void markRead(String username, Long notificationId) {
        User user = loadUser(username);
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.INTERNAL_ERROR));
        if (!n.getUser().getId().equals(user.getId()))
            throw new AppException(ErrorCode.UNAUTHORIZED);
        n.setRead(true);
        notificationRepository.save(n);
    }

    @Override
    @Transactional
    public void markAllRead(String username) {
        User user = loadUser(username);
        notificationRepository.markAllReadByUserId(user.getId());
    }

    private User loadUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .refAppointmentId(n.getRefAppointmentId())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
