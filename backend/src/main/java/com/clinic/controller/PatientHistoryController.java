package com.clinic.controller;

import com.clinic.constant.Role;
import com.clinic.dto.response.PatientHistoryResponse;
import com.clinic.entity.Appointment;
import com.clinic.entity.MedicalRecord;
import com.clinic.entity.User;
import com.clinic.exception.AppException;
import com.clinic.exception.ErrorCode;
import com.clinic.repository.AppointmentRepository;
import com.clinic.repository.MedicalRecordRepository;
import com.clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientHistoryController {

    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;

    @GetMapping("/{id}/history")
    public ResponseEntity<List<PatientHistoryResponse>> history(@PathVariable Long id, Authentication auth) {
        User caller = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        boolean isAdmin = caller.getRole() == Role.ADMIN;
        boolean isOwner = caller.getId().equals(id);
        boolean isDoctor = caller.getRole() == Role.DOCTOR;

        if (!isAdmin && !isOwner && !isDoctor) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (isDoctor) {
            boolean hasRelation = appointmentRepository
                    .findByPatientId(id)
                    .stream()
                    .anyMatch(a -> a.getDoctor().getId().equals(caller.getId()));
            if (!hasRelation) throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        List<Appointment> appointments = appointmentRepository.findByPatientId(id);

        List<Long> apptIds = appointments.stream().map(Appointment::getId).toList();
        Map<Long, MedicalRecord> recordMap = medicalRecordRepository.findByAppointmentIds(apptIds).stream()
                .collect(Collectors.toMap(r -> r.getAppointment().getId(), r -> r));

        List<PatientHistoryResponse> result = appointments.stream().map(a -> {
            MedicalRecord rec = recordMap.get(a.getId());
            return PatientHistoryResponse.builder()
                    .appointmentId(a.getId())
                    .doctorName(a.getDoctor().getFullName())
                    .specialtyName(a.getSpecialty() == null ? null : a.getSpecialty().getName())
                    .startTime(a.getStartTime())
                    .endTime(a.getEndTime())
                    .symptomDescription(a.getSymptomDescription())
                    .status(a.getStatus())
                    .doctorNote(a.getDoctorNote())
                    .diagnosis(rec != null ? rec.getDiagnosis() : null)
                    .clinicalNote(rec != null ? rec.getClinicalNote() : null)
                    .build();
        }).toList();

        return ResponseEntity.ok(result);
    }
}
