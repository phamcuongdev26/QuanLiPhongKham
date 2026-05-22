package com.clinic.dto.request;

import com.clinic.constant.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAppointmentStatusRequest {

    @NotNull
    private AppointmentStatus status;

    @Size(max = 2000)
    private String doctorNote;
}

