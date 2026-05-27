package com.clinic.service;

import com.clinic.dto.request.UpsertMedicalRecordRequest;
import com.clinic.dto.request.UpsertPrescriptionRequest;
import com.clinic.dto.response.PatientHistoryResponse;

public interface MedicalService {
    PatientHistoryResponse getMedicalHistory(String doctorUsername, Long appointmentId);
    void upsertMedicalRecord(String doctorUsername, Long appointmentId, UpsertMedicalRecordRequest request);
    void upsertPrescription(String doctorUsername, Long appointmentId, UpsertPrescriptionRequest request);
}

