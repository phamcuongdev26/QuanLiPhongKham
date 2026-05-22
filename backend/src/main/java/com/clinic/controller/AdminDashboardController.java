package com.clinic.controller;

import com.clinic.dto.response.ApiResponse;
import com.clinic.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> overview() {
        return ResponseEntity.ok(ApiResponse.builder()
                .code(200)
                .message("OK")
                .result(adminDashboardService.overview())
                .build());
    }
}

