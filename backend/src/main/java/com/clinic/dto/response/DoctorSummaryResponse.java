package com.clinic.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorSummaryResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Long specialtyId;
    private String specialtyName;
    private Long consultationFee;
    private String title;
    private String bio;
    @JsonProperty("isActive")
    private Boolean isActive;
}
