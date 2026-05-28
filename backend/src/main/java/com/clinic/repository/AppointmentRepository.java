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

    List<Appointment> findByDoctor_IdOrderByStartTimeDesc(Long doctorId);

    List<Appointment> findByDoctor_IdAndStartTimeBetweenOrderByStartTimeAsc(Long doctorId, LocalDateTime from, LocalDateTime to);

    @Query(value = """
            SELECT * FROM appointments
            WHERE doctor_id = :doctorId
              AND start_time BETWEEN :from AND :to
              AND status IN ('PENDING','CONFIRMED')
            ORDER BY start_time ASC
            """, nativeQuery = true)
    List<Appointment> findActiveByDoctorAndDateRange(@Param("doctorId") Long doctorId,
                                                     @Param("from") LocalDateTime from,
                                                     @Param("to") LocalDateTime to);

    @Query(value = """
            SELECT COUNT(*) > 0 FROM appointments a
            WHERE a.doctor_id = :doctorId
              AND a.status IN :activeStatuses
              AND a.start_time < :endTime
              AND a.end_time > :startTime
            """, nativeQuery = true)
    int existsOverlappingDoctorAppointment(@Param("doctorId") Long doctorId,
                                           @Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime,
                                           @Param("activeStatuses") List<String> activeStatuses);

    @Query(value = """
            SELECT * FROM appointments a
            WHERE a.doctor_id = :doctorId
              AND a.status IN :statuses
              AND a.start_time >= :from
            ORDER BY a.start_time ASC
            """, nativeQuery = true)
    List<Appointment> findUpcomingByDoctor(@Param("doctorId") Long doctorId,
                                           @Param("statuses") List<String> statuses,
                                           @Param("from") LocalDateTime from);

    @Query(value = """
            SELECT * FROM appointments
            WHERE doctor_id = :doctorId AND status IN :statuses
            ORDER BY start_time DESC
            """, nativeQuery = true)
    List<Appointment> findByDoctorAndStatuses(@Param("doctorId") Long doctorId,
                                              @Param("statuses") List<String> statuses);

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
