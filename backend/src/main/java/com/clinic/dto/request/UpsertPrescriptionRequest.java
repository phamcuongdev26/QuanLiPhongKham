package com.clinic.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpsertPrescriptionRequest {

    @Size(max = 2000)
    private String note;

    private List<UpsertPrescriptionItemRequest> items;
}

