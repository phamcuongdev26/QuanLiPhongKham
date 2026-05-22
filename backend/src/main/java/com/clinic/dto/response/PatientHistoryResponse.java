package com.clinic.dto.response;

import com.clinic.constant.AppointmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientHistoryResponse {
    private Long appointmentId;
    private String doctorName;
    private String specialtyName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String symptomDescription;
    private AppointmentStatus status;
    private String doctorNote;
    private String diagnosis;
    private String clinicalNote;
}
