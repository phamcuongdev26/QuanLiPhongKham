package com.clinic.controller;

import com.clinic.dto.request.CreateSpecialtyRequest;
import com.clinic.dto.request.UpdateSpecialtyRequest;
import com.clinic.dto.response.ApiResponse;
import com.clinic.dto.response.SpecialtyResponse;
import com.clinic.service.AuditLogService;
import com.clinic.service.SpecialtyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/specialties")
@RequiredArgsConstructor
public class AdminSpecialtyController {

    private final SpecialtyService specialtyService;
    private final AuditLogService auditLogService;

    @PostMapping
    public ResponseEntity<SpecialtyResponse> create(
            @Valid @RequestBody CreateSpecialtyRequest request,
            Authentication auth) {
        SpecialtyResponse created = specialtyService.create(request);
        auditLogService.log("CREATE", "SPECIALTY", created.getId(), created.getName(),
                auth.getName(), null, null, created,
                "Tạo chuyên khoa: " + created.getName());
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialtyResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSpecialtyRequest request,
            Authentication auth) {
        SpecialtyResponse old = specialtyService.getById(id);
        SpecialtyResponse updated = specialtyService.update(id, request);
        auditLogService.log("UPDATE", "SPECIALTY", id, updated.getName(),
                auth.getName(), null, old, updated,
                "Cập nhật chuyên khoa: " + updated.getName());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            Authentication auth) {
        SpecialtyResponse old = specialtyService.getById(id);
        specialtyService.delete(id);
        auditLogService.log("DELETE", "SPECIALTY", id, old.getName(),
                auth.getName(), null, old, null,
                "Xóa chuyên khoa: " + old.getName());
        return ResponseEntity.ok(ApiResponse.<Void>builder().code(200).message("Xóa chuyên khoa thành công").build());
    }
}
