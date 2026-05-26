package com.clinic.service;

import com.clinic.dto.request.AdminCreateDoctorRequest;
import com.clinic.dto.request.AdminUpdateDoctorRequest;
import com.clinic.dto.response.DoctorSummaryResponse;

import java.util.List;

public interface DoctorService {
    List<DoctorSummaryResponse> listBySpecialty(Long specialtyId);
    List<DoctorSummaryResponse> listAll();
    DoctorSummaryResponse getById(Long doctorId);
    DoctorSummaryResponse createDoctor(AdminCreateDoctorRequest request);
    DoctorSummaryResponse updateDoctor(Long doctorUserId, AdminUpdateDoctorRequest request);
    void deleteDoctor(Long doctorUserId);
    void toggleActive(Long doctorUserId);
}

