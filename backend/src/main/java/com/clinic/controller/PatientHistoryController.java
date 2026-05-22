package com.clinic.controller;

import com.clinic.dto.response.PatientHistoryResponse;
import com.clinic.entity.Appointment;
import com.clinic.entity.MedicalRecord;
import com.clinic.repository.AppointmentRepository;
import com.clinic.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{id}/history")
    public ResponseEntity<List<PatientHistoryResponse>> history(@PathVariable Long id) {
        List<Appointment> appointments = appointmentRepository.findByPatient_IdOrderByStartTimeDesc(id);

        List<Long> apptIds = appointments.stream().map(Appointment::getId).toList();
        Map<Long, MedicalRecord> recordMap = medicalRecordRepository.findByIdIn(apptIds).stream()
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
