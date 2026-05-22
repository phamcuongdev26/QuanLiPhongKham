package com.clinic.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorDayOffResponse {
    private Long id;
    private LocalDate dayOff;
    private String reason;
}

