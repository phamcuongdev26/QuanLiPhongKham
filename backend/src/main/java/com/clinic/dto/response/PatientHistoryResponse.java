package com.clinic.dto.response;

import com.clinic.constant.AppointmentStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientHistoryResponse {
    private Long appointmentId;
    private Long patientId;
    private String patientName;
    private String doctorName;
    private String specialtyName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String symptomDescription;
    private AppointmentStatus status;
    private String doctorNote;
    private String diagnosis;
    private String clinicalNote;
    private PrescriptionHistory prescription;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PrescriptionHistory {
        private String note;
        private LocalDateTime createdAt;
        private List<PrescriptionItemHistory> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PrescriptionItemHistory {
        private Long id;
        private String drugName;
        private String dosage;
        private String frequency;
        private String duration;
        private String instruction;
    }
}
