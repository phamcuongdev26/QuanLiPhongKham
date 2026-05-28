package com.clinic.service.impl;

import com.clinic.dto.request.CreateDayOffRequest;
import com.clinic.dto.request.UpsertDoctorShiftRequest;
import com.clinic.dto.response.DoctorDayOffResponse;
import com.clinic.dto.response.DoctorShiftResponse;
import com.clinic.entity.DoctorDayOff;
import com.clinic.entity.DoctorWorkShift;
import com.clinic.entity.User;
import com.clinic.exception.AppException;
import com.clinic.exception.ErrorCode;
import com.clinic.repository.DoctorDayOffRepository;
import com.clinic.repository.DoctorWorkShiftRepository;
import com.clinic.repository.UserRepository;
import com.clinic.service.DoctorScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final UserRepository userRepository;
    private final DoctorWorkShiftRepository doctorWorkShiftRepository;
    private final DoctorDayOffRepository doctorDayOffRepository;

    private User doctor(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
    }

    @Override
    @Transactional
    public DoctorShiftResponse addShift(String doctorUsername, UpsertDoctorShiftRequest request) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new AppException(ErrorCode.INVALID_TIME_RANGE);
        }
        User doctor = doctor(doctorUsername);
        DoctorWorkShift shift = DoctorWorkShift.builder()
                .doctor(doctor)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isActive(true)
                .build();
        shift = doctorWorkShiftRepository.save(shift);
        return DoctorShiftResponse.builder()
                .id(shift.getId())
                .dayOfWeek(shift.getDayOfWeek())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .isActive(shift.isActive())
                .build();
    }

    @Override
    public List<DoctorShiftResponse> listShifts(String doctorUsername) {
        User doctor = doctor(doctorUsername);
        return doctorWorkShiftRepository.findActiveByDoctorId(doctor.getId()).stream()
                .map(s -> DoctorShiftResponse.builder()
                        .id(s.getId())
                        .dayOfWeek(s.getDayOfWeek())
                        .startTime(s.getStartTime())
                        .endTime(s.getEndTime())
                        .isActive(s.isActive())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public DoctorDayOffResponse addDayOff(String doctorUsername, CreateDayOffRequest request) {
        User doctor = doctor(doctorUsername);
        if (doctorDayOffRepository.existsByDoctorIdAndDayOff(doctor.getId(), request.getDayOff())) {
            return doctorDayOffRepository.findByDoctorIdAndDayOff(doctor.getId(), request.getDayOff())
                    .map(d -> DoctorDayOffResponse.builder().id(d.getId()).dayOff(d.getDayOff()).reason(d.getReason()).build())
                    .orElseThrow(() -> new AppException(ErrorCode.INTERNAL_ERROR));
        }
        DoctorDayOff dayOff = DoctorDayOff.builder()
                .doctor(doctor)
                .dayOff(request.getDayOff())
                .reason(request.getReason())
                .build();
        dayOff = doctorDayOffRepository.save(dayOff);
        return DoctorDayOffResponse.builder()
                .id(dayOff.getId())
                .dayOff(dayOff.getDayOff())
                .reason(dayOff.getReason())
                .build();
    }

    @Override
    public List<DoctorDayOffResponse> listDaysOff(String doctorUsername) {
        User doctor = doctor(doctorUsername);
        return doctorDayOffRepository.findByDoctorId(doctor.getId()).stream()
                .map(d -> DoctorDayOffResponse.builder().id(d.getId()).dayOff(d.getDayOff()).reason(d.getReason()).build())
                .toList();
    }
}
