package com.clinic.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PrescriptionItemResponse {
    private Long id;
    private String drugName;
    private String dosage;
    private String frequency;
    private String duration;
    private String instruction;
}
