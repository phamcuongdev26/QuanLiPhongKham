package com.clinic.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PrescriptionResponse {
    private String note;
    private LocalDateTime createdAt;
    private List<PrescriptionItemResponse> items;
}
