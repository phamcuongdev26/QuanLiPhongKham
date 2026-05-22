package com.clinic.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpsertMedicalRecordRequest {

    @Size(max = 2000)
    private String diagnosis;

    @Size(max = 4000)
    private String clinicalNote;
}

