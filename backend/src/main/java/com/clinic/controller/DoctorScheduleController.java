package com.clinic.controller;

import com.clinic.dto.request.CreateDayOffRequest;
import com.clinic.dto.request.UpsertDoctorShiftRequest;
import com.clinic.dto.response.DoctorDayOffResponse;
import com.clinic.dto.response.DoctorShiftResponse;
import com.clinic.service.DoctorScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor/schedule")
@RequiredArgsConstructor
public class DoctorScheduleController {

    private final DoctorScheduleService doctorScheduleService;

    private String username() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? null : auth.getName();
    }

    @PostMapping("/shifts")
    public ResponseEntity<DoctorShiftResponse> addShift(@Valid @RequestBody UpsertDoctorShiftRequest request) {
        return ResponseEntity.ok(doctorScheduleService.addShift(username(), request));
    }

    @GetMapping("/shifts")
    public ResponseEntity<List<DoctorShiftResponse>> shifts() {
        return ResponseEntity.ok(doctorScheduleService.listShifts(username()));
    }

    @PostMapping("/days-off")
    public ResponseEntity<DoctorDayOffResponse> addDayOff(@Valid @RequestBody CreateDayOffRequest request) {
        return ResponseEntity.ok(doctorScheduleService.addDayOff(username(), request));
    }

    @GetMapping("/days-off")
    public ResponseEntity<List<DoctorDayOffResponse>> daysOff() {
        return ResponseEntity.ok(doctorScheduleService.listDaysOff(username()));
    }
}

