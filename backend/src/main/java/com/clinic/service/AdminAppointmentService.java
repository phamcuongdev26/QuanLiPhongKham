package com.clinic.service;

import com.clinic.dto.request.UpdateAppointmentStatusRequest;
import com.clinic.dto.response.AppointmentResponse;

import java.util.List;

public interface AdminAppointmentService {
    List<AppointmentResponse> listAll();
    AppointmentResponse updateStatus(Long id, UpdateAppointmentStatusRequest request);
}

