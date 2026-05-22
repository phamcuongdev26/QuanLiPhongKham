package com.clinic.service;

import com.clinic.dto.response.AppointmentResponse;

import java.util.List;

public interface AdminAppointmentService {
    List<AppointmentResponse> listAll();
}

