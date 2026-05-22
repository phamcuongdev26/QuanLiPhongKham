package com.clinic.controller;

import com.clinic.dto.response.TimeSlotResponse;
import com.clinic.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping("/doctor-slots")
    public ResponseEntity<List<TimeSlotResponse>> doctorSlots(@RequestParam Long doctorId,
                                                             @RequestParam LocalDate date,
                                                             @RequestParam(defaultValue = "30") int slotMinutes) {
        return ResponseEntity.ok(availabilityService.getDoctorAvailableSlots(doctorId, date, slotMinutes));
    }
}

