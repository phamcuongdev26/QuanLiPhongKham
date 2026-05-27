package com.clinic.service.impl;

import com.clinic.constant.AppointmentStatus;
import com.clinic.dto.request.UpsertMedicalRecordRequest;
import com.clinic.dto.request.UpsertPrescriptionItemRequest;
import com.clinic.dto.request.UpsertPrescriptionRequest;
import com.clinic.dto.response.MedicalRecordResponse;
import com.clinic.dto.response.PrescriptionItemResponse;
import com.clinic.dto.response.PrescriptionResponse;
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
        if (appointment.getStatus() == AppointmentStatus.CANCELED
                || appointment.getStatus() == AppointmentStatus.REJECTED
                || appointment.getStatus() == AppointmentStatus.PENDING) {
            throw new AppException(ErrorCode.APPOINTMENT_INVALID_STATUS);
        }
        return appointment;
    }


    @Override
    @Transactional
    public void upsertMedicalRecord(String doctorUsername, Long appointmentId, UpsertMedicalRecordRequest request) {
        User doc = doctor(doctorUsername);
        Appointment appointment = doctorAppointment(doc, appointmentId);

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
        User doc = doctor(doctorUsername);
        Appointment appointment = doctorAppointment(doc, appointmentId);

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

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordResponse> getDoctorRecords(String doctorUsername) {
        User doc = doctor(doctorUsername);
        List<Appointment> appointments = appointmentRepository.findByDoctorAndStatuses(
                doc.getId(),
                List.of(AppointmentStatus.CONFIRMED.name(), AppointmentStatus.COMPLETED.name())
        );
        return appointments.stream().map(a -> {
            MedicalRecord rec = medicalRecordRepository.findById(a.getId()).orElse(null);
            return toResponseFromAppointment(a, rec);
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalRecordResponse getRecord(String doctorUsername, Long appointmentId) {
        User doc = doctor(doctorUsername);
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));
        if (!appointment.getDoctor().getId().equals(doc.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        MedicalRecord record = medicalRecordRepository.findById(appointmentId).orElse(null);
        return toResponseFromAppointment(appointment, record);
    }

    private MedicalRecordResponse toResponseFromAppointment(Appointment a, MedicalRecord rec) {
        Prescription prx = prescriptionRepository.findByAppointment_Id(a.getId()).orElse(null);
        PrescriptionResponse prxResponse = null;
        if (prx != null) {
            List<PrescriptionItemResponse> items = prx.getItems().stream()
                    .map(i -> PrescriptionItemResponse.builder()
                            .id(i.getId()).drugName(i.getDrugName()).dosage(i.getDosage())
                            .frequency(i.getFrequency()).duration(i.getDuration()).instruction(i.getInstruction())
                            .build())
                    .toList();
            prxResponse = PrescriptionResponse.builder().note(prx.getNote()).createdAt(prx.getCreatedAt()).items(items).build();
        }
        return MedicalRecordResponse.builder()
                .appointmentId(a.getId())
                .patientName(a.getPatient().getFullName())
                .patientPhone(a.getPatient().getPhoneNumber())
                .specialtyName(a.getSpecialty() == null ? null : a.getSpecialty().getName())
                .startTime(a.getStartTime())
                .status(a.getStatus())
                .symptomDescription(a.getSymptomDescription())
                .diagnosis(rec != null ? rec.getDiagnosis() : null)
                .clinicalNote(rec != null ? rec.getClinicalNote() : null)
                .prescription(prxResponse)
                .build();
    }
}
