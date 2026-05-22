package com.clinic.service;

import com.clinic.dto.response.TimeSlotResponse;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityService {
    List<TimeSlotResponse> getDoctorAvailableSlots(Long doctorId, LocalDate date, int slotMinutes);
}

