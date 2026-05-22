package com.clinic.controller;

import com.clinic.dto.request.CreateMedicalRecordRequest;
import com.clinic.dto.request.UpsertMedicalRecordRequest;
import com.clinic.dto.response.ApiResponse;
import com.clinic.service.MedicalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalService medicalService;

    private String username() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? null : auth.getName();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> create(@Valid @RequestBody CreateMedicalRecordRequest request) {
        UpsertMedicalRecordRequest upsert = UpsertMedicalRecordRequest.builder()
                .diagnosis(request.getDiagnosis())
                .clinicalNote(request.getClinicalNote())
                .build();
        medicalService.upsertMedicalRecord(username(), request.getAppointmentId(), upsert);
        return ResponseEntity.ok(ApiResponse.<Void>builder().code(200).message("Lưu hồ sơ bệnh án thành công").build());
    }
}
