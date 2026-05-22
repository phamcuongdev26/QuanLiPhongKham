package com.clinic.dto.response;

import com.clinic.constant.AppointmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private Long specialtyId;
    private String specialtyName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String symptomDescription;
    private AppointmentStatus status;
    private String doctorNote;
}

