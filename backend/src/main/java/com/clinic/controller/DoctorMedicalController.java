package com.clinic.controller;

import com.clinic.dto.request.UpsertMedicalRecordRequest;
import com.clinic.dto.request.UpsertPrescriptionRequest;
import com.clinic.dto.response.ApiResponse;
import com.clinic.dto.response.MedicalRecordResponse;
import com.clinic.service.MedicalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor/medical")
@RequiredArgsConstructor
public class DoctorMedicalController {

    private final MedicalService medicalService;

    private String username() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? null : auth.getName();
    }

    @GetMapping("/records")
    public ResponseEntity<List<MedicalRecordResponse>> getRecords() {
        return ResponseEntity.ok(medicalService.getDoctorRecords(username()));
    }

    @GetMapping("/appointments/{id}/record")
    public ResponseEntity<MedicalRecordResponse> getRecord(@PathVariable Long id) {
        return ResponseEntity.ok(medicalService.getRecord(username(), id));
    }

    @PutMapping("/appointments/{id}/record")
    public ResponseEntity<ApiResponse<Void>> upsertRecord(@PathVariable Long id,
                                                          @Valid @RequestBody UpsertMedicalRecordRequest request) {
        medicalService.upsertMedicalRecord(username(), id, request);
        return ResponseEntity.ok(ApiResponse.<Void>builder().code(200).message("Lưu hồ sơ bệnh án thành công").build());
    }

    @PutMapping("/appointments/{id}/prescription")
    public ResponseEntity<ApiResponse<Void>> upsertPrescription(@PathVariable Long id,
                                                                @Valid @RequestBody UpsertPrescriptionRequest request) {
        medicalService.upsertPrescription(username(), id, request);
        return ResponseEntity.ok(ApiResponse.<Void>builder().code(200).message("Lưu đơn thuốc thành công").build());
    }
}
