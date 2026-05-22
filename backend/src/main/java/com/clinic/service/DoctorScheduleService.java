package com.clinic.service;

import com.clinic.dto.request.CreateDayOffRequest;
import com.clinic.dto.request.UpsertDoctorShiftRequest;
import com.clinic.dto.response.DoctorDayOffResponse;
import com.clinic.dto.response.DoctorShiftResponse;

import java.util.List;

public interface DoctorScheduleService {
    DoctorShiftResponse addShift(String doctorUsername, UpsertDoctorShiftRequest request);
    List<DoctorShiftResponse> listShifts(String doctorUsername);
    DoctorDayOffResponse addDayOff(String doctorUsername, CreateDayOffRequest request);
    List<DoctorDayOffResponse> listDaysOff(String doctorUsername);
}

