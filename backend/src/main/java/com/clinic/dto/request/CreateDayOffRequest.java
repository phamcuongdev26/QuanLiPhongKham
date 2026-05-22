package com.clinic.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDayOffRequest {

    @NotNull
    private LocalDate dayOff;

    private String reason;
}

