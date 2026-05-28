package com.clinic.repository;

import com.clinic.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    @Query(value = "SELECT * FROM prescriptions WHERE appointment_id = :appointmentId LIMIT 1", nativeQuery = true)
    Optional<Prescription> findByAppointmentId(@Param("appointmentId") Long appointmentId);
}
