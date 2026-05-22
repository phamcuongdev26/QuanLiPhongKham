package com.clinic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUpdateDoctorRequest {

    @NotBlank
    private String fullName;

    private String phoneNumber;

    @NotNull
    private Long specialtyId;

    private String title;

    private String bio;

    private Long consultationFee;

    private Boolean isActive;
}
