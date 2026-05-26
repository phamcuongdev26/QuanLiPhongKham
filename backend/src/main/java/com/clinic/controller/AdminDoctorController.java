package com.clinic.controller;

import com.clinic.dto.request.AdminCreateDoctorRequest;
import com.clinic.dto.request.AdminUpdateDoctorRequest;
import com.clinic.dto.response.ApiResponse;
import com.clinic.dto.response.DoctorSummaryResponse;
import com.clinic.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/doctors")
@RequiredArgsConstructor
public class AdminDoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<List<DoctorSummaryResponse>> list() {
        return ResponseEntity.ok(doctorService.listAll());
    }

    @PostMapping
    public ResponseEntity<DoctorSummaryResponse> create(@Valid @RequestBody AdminCreateDoctorRequest request) {
        return ResponseEntity.ok(doctorService.createDoctor(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorSummaryResponse> update(@PathVariable Long id,
                                                        @Valid @RequestBody AdminUpdateDoctorRequest request) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, request));
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ApiResponse<Void>> toggleActive(@PathVariable Long id) {
        doctorService.toggleActive(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().code(200).message("Cập nhật trạng thái thành công").build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().code(200).message("Xóa bác sĩ thành công").build());
    }
}

