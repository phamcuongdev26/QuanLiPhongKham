package com.clinic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpsertPrescriptionItemRequest {

    @NotBlank
    @Size(max = 200)
    private String drugName;

    @Size(max = 200)
    private String dosage;

    @Size(max = 200)
    private String frequency;

    @Size(max = 200)
    private String duration;

    @Size(max = 500)
    private String instruction;
}

