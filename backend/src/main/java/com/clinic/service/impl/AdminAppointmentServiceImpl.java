package com.clinic.service.impl;

import com.clinic.dto.response.AppointmentResponse;
import com.clinic.entity.Appointment;
import com.clinic.repository.AppointmentRepository;
import com.clinic.service.AdminAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAppointmentServiceImpl implements AdminAppointmentService {

    private final AppointmentRepository appointmentRepository;

    private AppointmentResponse toResponse(Appointment a) {
        return AppointmentResponse.builder()
                .id(a.getId())
                .patientId(a.getPatient().getId())
                .patientName(a.getPatient().getFullName())
                .doctorId(a.getDoctor().getId())
                .doctorName(a.getDoctor().getFullName())
                .specialtyId(a.getSpecialty() == null ? null : a.getSpecialty().getId())
                .specialtyName(a.getSpecialty() == null ? null : a.getSpecialty().getName())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .symptomDescription(a.getSymptomDescription())
                .status(a.getStatus())
                .doctorNote(a.getDoctorNote())
                .build();
    }

    @Override
    public List<AppointmentResponse> listAll() {
        return appointmentRepository.findAll().stream()
                .sorted((a, b) -> b.getStartTime().compareTo(a.getStartTime()))
                .map(this::toResponse)
                .toList();
    }
}

