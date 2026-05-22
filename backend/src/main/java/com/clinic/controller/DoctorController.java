package com.clinic.controller;

import com.clinic.dto.response.DoctorSummaryResponse;
import com.clinic.dto.response.TimeSlotResponse;
import com.clinic.service.AvailabilityService;
import com.clinic.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final AvailabilityService availabilityService;

    @GetMapping
    public ResponseEntity<List<DoctorSummaryResponse>> list(@RequestParam(required = false) Long specialtyId) {
        if (specialtyId == null) {
            return ResponseEntity.ok(doctorService.listAll());
        }
        return ResponseEntity.ok(doctorService.listBySpecialty(specialtyId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorSummaryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getById(id));
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<List<TimeSlotResponse>> slots(@PathVariable Long id,
                                                        @RequestParam LocalDate date,
                                                        @RequestParam(defaultValue = "30") int slotMinutes) {
        return ResponseEntity.ok(availabilityService.getDoctorAvailableSlots(id, date, slotMinutes));
    }
}

