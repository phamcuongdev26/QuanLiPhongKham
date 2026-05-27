package com.clinic.repository;

import com.clinic.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByIdIn(List<Long> ids);

    @Query(value = "SELECT * FROM medical_records mr JOIN appointments a ON a.id = mr.appointment_id WHERE a.doctor_id = :doctorId ORDER BY a.start_time DESC", nativeQuery = true)
    List<MedicalRecord> findByAppointment_Doctor_Id(@Param("doctorId") Long doctorId);
}
