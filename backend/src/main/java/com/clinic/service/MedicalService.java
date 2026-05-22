package com.clinic.service;

import com.clinic.dto.request.UpsertMedicalRecordRequest;
import com.clinic.dto.request.UpsertPrescriptionRequest;

public interface MedicalService {
    void upsertMedicalRecord(String doctorUsername, Long appointmentId, UpsertMedicalRecordRequest request);
    void upsertPrescription(String doctorUsername, Long appointmentId, UpsertPrescriptionRequest request);
}

