package com.clinic.dto.response;

import com.clinic.constant.AppointmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MedicalRecordResponse {
    private Long appointmentId;
    private String patientName;
    private String patientPhone;
    private String specialtyName;
    private LocalDateTime startTime;
    private AppointmentStatus status;
    private String symptomDescription;
    private String diagnosis;
    private String clinicalNote;
    private PrescriptionResponse prescription;
}
