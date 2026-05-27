package com.clinic.service.impl;

import com.clinic.constant.AppointmentStatus;
import com.clinic.dto.request.UpsertMedicalRecordRequest;
import com.clinic.dto.request.UpsertPrescriptionItemRequest;
import com.clinic.dto.request.UpsertPrescriptionRequest;
import com.clinic.dto.response.PatientHistoryResponse;
import com.clinic.entity.Appointment;
import com.clinic.entity.MedicalRecord;
import com.clinic.entity.Prescription;
import com.clinic.entity.PrescriptionItem;
import com.clinic.entity.User;
import com.clinic.exception.AppException;
import com.clinic.exception.ErrorCode;
import com.clinic.repository.AppointmentRepository;
import com.clinic.repository.MedicalRecordRepository;
import com.clinic.repository.PrescriptionRepository;
import com.clinic.repository.UserRepository;
import com.clinic.service.MedicalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalServiceImpl implements MedicalService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionRepository prescriptionRepository;

    private User doctor(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
    }

    private Appointment doctorAppointment(User doctor, Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));
        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new AppException(ErrorCode.APPOINTMENT_INVALID_STATUS);
        }
        return appointment;
    }

    @Override
    @Transactional(readOnly = true)
    public PatientHistoryResponse getMedicalHistory(String doctorUsername, Long appointmentId) {
        User doctor = doctor(doctorUsername);
        Appointment appointment = doctorAppointment(doctor, appointmentId);
        MedicalRecord record = medicalRecordRepository.findById(appointment.getId()).orElse(null);
        Prescription prescription = prescriptionRepository.findByIdInWithItems(List.of(appointment.getId()))
                .stream()
                .findFirst()
                .orElse(null);

        return PatientHistoryResponse.builder()
                .appointmentId(appointment.getId())
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getFullName())
                .doctorName(appointment.getDoctor().getFullName())
                .specialtyName(appointment.getSpecialty() == null ? null : appointment.getSpecialty().getName())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .symptomDescription(appointment.getSymptomDescription())
                .status(appointment.getStatus())
                .doctorNote(appointment.getDoctorNote())
                .diagnosis(record == null ? null : record.getDiagnosis())
                .clinicalNote(record == null ? null : record.getClinicalNote())
                .prescription(toPrescriptionHistory(prescription))
                .build();
    }

    @Override
    @Transactional
    public void upsertMedicalRecord(String doctorUsername, Long appointmentId, UpsertMedicalRecordRequest request) {
        User doctor = doctor(doctorUsername);
        Appointment appointment = doctorAppointment(doctor, appointmentId);

        MedicalRecord record = medicalRecordRepository.findById(appointment.getId()).orElse(null);
        if (record == null) {
            record = MedicalRecord.builder()
                    .appointment(appointment)
                    .diagnosis(request.getDiagnosis())
                    .clinicalNote(request.getClinicalNote())
                    .build();
        } else {
            record.setDiagnosis(request.getDiagnosis());
            record.setClinicalNote(request.getClinicalNote());
        }
        medicalRecordRepository.save(record);
    }

    @Override
    @Transactional
    public void upsertPrescription(String doctorUsername, Long appointmentId, UpsertPrescriptionRequest request) {
        User doctor = doctor(doctorUsername);
        Appointment appointment = doctorAppointment(doctor, appointmentId);

        Prescription prescription = prescriptionRepository.findById(appointment.getId()).orElse(null);
        if (prescription == null) {
            prescription = Prescription.builder()
                    .appointment(appointment)
                    .note(request.getNote())
                    .build();
        } else {
            prescription.setNote(request.getNote());
            prescription.getItems().clear();
        }

        List<UpsertPrescriptionItemRequest> items = request.getItems() == null ? List.of() : request.getItems();
        for (UpsertPrescriptionItemRequest item : items) {
            prescription.getItems().add(PrescriptionItem.builder()
                    .prescription(prescription)
                    .drugName(item.getDrugName())
                    .dosage(item.getDosage())
                    .frequency(item.getFrequency())
                    .duration(item.getDuration())
                    .instruction(item.getInstruction())
                    .build());
        }
        prescriptionRepository.save(prescription);
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

