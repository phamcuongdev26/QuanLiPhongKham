package com.clinic.repository;

import com.clinic.constant.AppointmentStatus;
import com.clinic.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatient_IdOrderByStartTimeDesc(Long patientId);

    List<Appointment> findByDoctor_IdAndStartTimeBetweenOrderByStartTimeAsc(Long doctorId, LocalDateTime from, LocalDateTime to);

    @Query(value = """
            SELECT COUNT(*) > 0 FROM appointments a
            WHERE a.doctor_id = :doctorId
              AND a.status IN :activeStatuses
              AND a.start_time < :endTime
              AND a.end_time > :startTime
            """, nativeQuery = true)
    boolean existsOverlappingDoctorAppointment(@Param("doctorId") Long doctorId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime,
                                               @Param("activeStatuses") List<String> activeStatuses);

    long countByStatus(AppointmentStatus status);

    @Query(value = """
            SELECT COALESCE(SUM(dp.consultation_fee), 0)
            FROM appointments a
            JOIN doctor_profiles dp ON dp.user_id = a.doctor_id
            WHERE a.status = :status
            """, nativeQuery = true)
    long sumRevenueByCompletedStatus(@Param("status") String status);

    @Query(value = """
            SELECT a.doctor_id
            FROM appointments a
            WHERE a.status = :status
            GROUP BY a.doctor_id
            ORDER BY COUNT(a.id) DESC
            """, nativeQuery = true)
    List<Long> findDoctorIdsByCompletedCountDesc(@Param("status") String status);
}
