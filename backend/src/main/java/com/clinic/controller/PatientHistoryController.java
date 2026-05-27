package com.clinic.controller;

import com.clinic.constant.Role;
import com.clinic.constant.AppointmentStatus;
import com.clinic.dto.response.PatientHistoryResponse;
import com.clinic.entity.Appointment;
import com.clinic.entity.MedicalRecord;
import com.clinic.entity.Prescription;
import com.clinic.entity.User;
import com.clinic.repository.AppointmentRepository;
import com.clinic.repository.MedicalRecordRepository;
import com.clinic.repository.PrescriptionRepository;
import com.clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final PrescriptionRepository prescriptionRepository;
    private final UserRepository userRepository;

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth == null ? null : auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<PatientHistoryResponse>> history(@PathVariable Long id) {
        User currentUser = currentUser();
        if (currentUser.getRole() == Role.PATIENT && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Access denied");
        }
        if (currentUser.getRole() != Role.PATIENT && currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Access denied");
        }

        List<Appointment> appointments = appointmentRepository.findByPatient_IdOrderByStartTimeDesc(id);

        List<Long> apptIds = appointments.stream().map(Appointment::getId).toList();
        Map<Long, MedicalRecord> recordMap = medicalRecordRepository.findByIdIn(apptIds).stream()
                .collect(Collectors.toMap(r -> r.getAppointment().getId(), r -> r));
        Map<Long, Prescription> prescriptionMap = apptIds.isEmpty()
                ? Map.of()
                : prescriptionRepository.findByIdInWithItems(apptIds).stream()
                .collect(Collectors.toMap(p -> p.getAppointment().getId(), p -> p));

        List<PatientHistoryResponse> result = appointments.stream().map(a -> {
            boolean completed = a.getStatus() == AppointmentStatus.COMPLETED;
            MedicalRecord rec = completed ? recordMap.get(a.getId()) : null;
            Prescription prescription = completed ? prescriptionMap.get(a.getId()) : null;
            return PatientHistoryResponse.builder()
                    .appointmentId(a.getId())
                    .patientId(a.getPatient().getId())
                    .patientName(a.getPatient().getFullName())
                    .doctorName(a.getDoctor().getFullName())
                    .specialtyName(a.getSpecialty() == null ? null : a.getSpecialty().getName())
                    .startTime(a.getStartTime())
                    .endTime(a.getEndTime())
                    .symptomDescription(a.getSymptomDescription())
                    .status(a.getStatus())
                    .doctorNote(a.getDoctorNote())
                    .diagnosis(rec != null ? rec.getDiagnosis() : null)
                    .clinicalNote(rec != null ? rec.getClinicalNote() : null)
                    .prescription(toPrescriptionHistory(prescription))
                    .build();
        }).toList();

        return ResponseEntity.ok(result);
    }

    private PatientHistoryResponse.PrescriptionHistory toPrescriptionHistory(Prescription prescription) {
        if (prescription == null) {
            return null;
        }
        return PatientHistoryResponse.PrescriptionHistory.builder()
                .note(prescription.getNote())
                .createdAt(prescription.getCreatedAt())
                .items(prescription.getItems().stream()
                        .map(item -> PatientHistoryResponse.PrescriptionItemHistory.builder()
                                .id(item.getId())
                                .drugName(item.getDrugName())
                                .dosage(item.getDosage())
                                .frequency(item.getFrequency())
                                .duration(item.getDuration())
                                .instruction(item.getInstruction())
                                .build())
                        .toList())
                .build();
    }
}
