package com.clinic.controller;

import com.clinic.dto.response.ApiResponse;
import com.clinic.dto.response.NotificationResponse;
import com.clinic.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    private String username() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? null : auth.getName();
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> list() {
        return ResponseEntity.ok(notificationService.listForUser(username()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> unreadCount() {
        return ResponseEntity.ok(ApiResponse.<Long>builder()
                .code(200).message("OK")
                .result(notificationService.countUnread(username()))
                .build());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(@PathVariable Long id) {
        notificationService.markRead(username(), id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().code(200).message("Đã đánh dấu đã đọc").build());
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllRead() {
        notificationService.markAllRead(username());
        return ResponseEntity.ok(ApiResponse.<Void>builder().code(200).message("Đã đọc tất cả").build());
    }
}
