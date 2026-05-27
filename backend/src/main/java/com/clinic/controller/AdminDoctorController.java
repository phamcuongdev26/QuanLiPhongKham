package com.clinic.controller;

import com.clinic.dto.request.AdminCreateDoctorRequest;
import com.clinic.dto.request.AdminUpdateDoctorRequest;
import com.clinic.dto.response.ApiResponse;
import com.clinic.dto.response.DoctorSummaryResponse;
import com.clinic.service.AuditLogService;
import com.clinic.service.DoctorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/doctors")
@RequiredArgsConstructor
public class AdminDoctorController {

    private final DoctorService doctorService;
    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<DoctorSummaryResponse>> list() {
        return ResponseEntity.ok(doctorService.listAll());
    }

    @PostMapping
    public ResponseEntity<DoctorSummaryResponse> create(
            @Valid @RequestBody AdminCreateDoctorRequest request,
            Authentication auth, HttpServletRequest httpRequest) {
        DoctorSummaryResponse created = doctorService.createDoctor(request);
        auditLogService.log("CREATE", "DOCTOR", created.getId(), created.getFullName(),
                auth.getName(), getClientIp(httpRequest), null, created,
                "Tạo bác sĩ: " + created.getFullName());
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorSummaryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateDoctorRequest request,
            Authentication auth, HttpServletRequest httpRequest) {
        DoctorSummaryResponse old = doctorService.getById(id);
        DoctorSummaryResponse updated = doctorService.updateDoctor(id, request);
        auditLogService.log("UPDATE", "DOCTOR", id, updated.getFullName(),
                auth.getName(), getClientIp(httpRequest), old, updated,
                "Cập nhật bác sĩ: " + updated.getFullName());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            Authentication auth, HttpServletRequest httpRequest) {
        DoctorSummaryResponse old = doctorService.getById(id);
        doctorService.deleteDoctor(id);
        auditLogService.log("DELETE", "DOCTOR", id, old.getFullName(),
                auth.getName(), getClientIp(httpRequest), old, null,
                "Xóa bác sĩ: " + old.getFullName());
        return ResponseEntity.ok(ApiResponse.<Void>builder().code(200).message("Xóa bác sĩ thành công").build());
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        return ip;
    }
}
