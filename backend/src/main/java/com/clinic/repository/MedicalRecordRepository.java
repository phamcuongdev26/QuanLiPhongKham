package com.clinic.repository;

import com.clinic.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    @Query(value = "SELECT * FROM medical_records WHERE appointment_id IN :appointmentIds", nativeQuery = true)
    List<MedicalRecord> findByAppointmentIds(@Param("appointmentIds") List<Long> appointmentIds);

    @Query(value = "SELECT * FROM medical_records mr JOIN appointments a ON a.id = mr.appointment_id WHERE a.doctor_id = :doctorId ORDER BY a.start_time DESC", nativeQuery = true)
    List<MedicalRecord> findByDoctorId(@Param("doctorId") Long doctorId);
}
