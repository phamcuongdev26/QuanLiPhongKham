package com.clinic.controller;

import com.clinic.dto.response.AppointmentResponse;
import com.clinic.service.AdminAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/appointments")
@RequiredArgsConstructor
public class AdminAppointmentController {

    private final AdminAppointmentService adminAppointmentService;

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> list() {
        return ResponseEntity.ok(adminAppointmentService.listAll());
    }
}

