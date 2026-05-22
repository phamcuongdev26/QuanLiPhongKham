package com.clinic.controller;

import com.clinic.dto.request.CreateSpecialtyRequest;
import com.clinic.dto.response.ApiResponse;
import com.clinic.dto.response.SpecialtyResponse;
import com.clinic.service.SpecialtyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/specialties")
@RequiredArgsConstructor
public class AdminSpecialtyController {

    private final SpecialtyService specialtyService;

    @GetMapping
    public ResponseEntity<List<SpecialtyResponse>> listAll() {
        return ResponseEntity.ok(specialtyService.listAll());
    }

    @PostMapping
    public ResponseEntity<SpecialtyResponse> create(@Valid @RequestBody CreateSpecialtyRequest request) {
        return ResponseEntity.ok(specialtyService.create(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        specialtyService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().code(200).message("Xóa chuyên khoa thành công").build());
    }
}

