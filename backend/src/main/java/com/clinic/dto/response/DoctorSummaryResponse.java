package com.clinic.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorSummaryResponse {
    private Long id;
    private String fullName;
    private String email;
    private Long specialtyId;
    private String specialtyName;
    private Long consultationFee;
    private String title;
    private String bio;
}

