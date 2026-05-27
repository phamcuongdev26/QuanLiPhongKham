package com.clinic.service;

import com.clinic.dto.request.CreateAppointmentRequest;
import com.clinic.dto.request.UpdateAppointmentStatusRequest;
import com.clinic.dto.response.AppointmentResponse;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {
    AppointmentResponse createForPatient(String patientUsername, CreateAppointmentRequest request);
    List<AppointmentResponse> listForPatient(String patientUsername);
    void cancelByPatient(String patientUsername, Long appointmentId);

    AppointmentResponse getById(Long appointmentId);

    List<AppointmentResponse> listDoctorToday(String doctorUsername, LocalDate date);
    AppointmentResponse updateDoctorStatus(String doctorUsername, Long appointmentId, UpdateAppointmentStatusRequest request);
}

