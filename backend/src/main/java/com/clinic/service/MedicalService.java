package com.clinic.service;

import com.clinic.dto.request.UpsertMedicalRecordRequest;
import com.clinic.dto.request.UpsertPrescriptionRequest;
import com.clinic.dto.response.MedicalRecordResponse;

import java.util.List;

public interface MedicalService {
    void upsertMedicalRecord(String doctorUsername, Long appointmentId, UpsertMedicalRecordRequest request);
    void upsertPrescription(String doctorUsername, Long appointmentId, UpsertPrescriptionRequest request);

    List<MedicalRecordResponse> getDoctorRecords(String doctorUsername);
    MedicalRecordResponse getRecord(String doctorUsername, Long appointmentId);
}
