package com.clinic.dto.response;

import com.clinic.constant.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private Long refAppointmentId;
    private boolean isRead;
    private LocalDateTime createdAt;
}
