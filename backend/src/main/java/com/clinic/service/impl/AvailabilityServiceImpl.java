package com.clinic.service.impl;

import com.clinic.constant.AppointmentStatus;
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
import java.util.Comparator;
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

        DayOfWeek dow = date.getDayOfWeek();
        List<DoctorWorkShift> shifts = doctorWorkShiftRepository.findByDoctor_IdAndIsActiveTrue(doctorId).stream()
                .filter(s -> s.getDayOfWeek() == dow)
                .toList();
        if (shifts.isEmpty()) {
            return List.of();
        }

        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.atTime(LocalTime.MAX);
        List<Appointment> appointments = appointmentRepository
                .findByDoctor_IdAndStartTimeBetweenOrderByStartTimeAsc(doctorId, from, to).stream()
                .filter(a -> a.getStatus() == AppointmentStatus.PENDING || a.getStatus() == AppointmentStatus.CONFIRMED)
                .sorted(Comparator.comparing(Appointment::getStartTime))
                .toList();

        List<TimeSlotResponse> slots = new ArrayList<>();
        for (DoctorWorkShift shift : shifts) {
            LocalDateTime cursor = LocalDateTime.of(date, shift.getStartTime());
            LocalDateTime shiftEnd = LocalDateTime.of(date, shift.getEndTime());
            while (!cursor.plusMinutes(minutes).isAfter(shiftEnd)) {
                LocalDateTime end = cursor.plusMinutes(minutes);
                boolean overlap = false;
                for (Appointment a : appointments) {
                    if (a.getStartTime().isBefore(end) && a.getEndTime().isAfter(cursor)) {
                        overlap = true;
                        break;
                    }
                }
                if (!overlap) {
                    slots.add(TimeSlotResponse.builder().startTime(cursor).endTime(end).build());
                }
                cursor = end;
            }
        }
        return slots;
    }
}

