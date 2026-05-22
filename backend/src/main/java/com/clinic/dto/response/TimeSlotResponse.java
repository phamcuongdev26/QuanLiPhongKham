package com.clinic.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotResponse {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

