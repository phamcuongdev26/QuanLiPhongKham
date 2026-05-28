package com.clinic.service.impl;

import com.clinic.dto.response.TimeSlotResponse;
import com.clinic.entity.Appointment;
import com.clinic.entity.DoctorWorkShift;
import com.clinic.exception.AppException;
import com.clinic.exception.ErrorCode;
import com.clinic.repository.AppointmentRepository;
import com.clinic.repository.DoctorDayOffRepository;
import com.clinic.repository.DoctorWorkShiftRepository;
import com.clinic.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final DoctorWorkShiftRepository doctorWorkShiftRepository;
    private final DoctorDayOffRepository doctorDayOffRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public List<TimeSlotResponse> getDoctorAvailableSlots(Long doctorId, LocalDate date, int slotMinutes) {
        if (date == null) {
            throw new AppException(ErrorCode.INVALID_TIME_RANGE);
        }
        int minutes = slotMinutes <= 0 ? 30 : slotMinutes;

        if (doctorDayOffRepository.existsByDoctor_IdAndDayOff(doctorId, date)) {
            return List.of();
        }

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<DoctorWorkShift> workingShifts = doctorWorkShiftRepository
                .findByDoctor_IdAndDayOfWeekAndIsActiveTrueOrderByStartTimeAsc(doctorId, dayOfWeek);
        if (workingShifts.isEmpty()) {
            return List.of();
        }

        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.atTime(LocalTime.MAX);
        List<Appointment> appointments = appointmentRepository
                .findActiveByDoctorAndDateRange(doctorId, from, to);

        List<TimeSlotResponse> availableSlots = new ArrayList<>();
        for (DoctorWorkShift shift : workingShifts) {
            LocalDateTime cursor = LocalDateTime.of(date, shift.getStartTime());
            LocalDateTime shiftEnd = LocalDateTime.of(date, shift.getEndTime());
            while (!cursor.plusMinutes(minutes).isAfter(shiftEnd)) {
                LocalDateTime slotEnd = cursor.plusMinutes(minutes);
                boolean overlap = false;
                for (Appointment a : appointments) {
                    if (a.getStartTime().isBefore(slotEnd) && a.getEndTime().isAfter(cursor)) {
                        overlap = true;
                        break;
                    }
                }
                if (!overlap) {
                    availableSlots.add(TimeSlotResponse.builder().startTime(cursor).endTime(slotEnd).build());
                }
                cursor = slotEnd;
            }
        }
        return availableSlots;
    }
}
