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

    @Query("""
            SELECT COUNT(a) > 0 FROM Appointment a
            WHERE a.doctor.id = :doctorId
              AND a.status IN :activeStatuses
              AND a.startTime < :endTime
              AND a.endTime > :startTime
            """)
    boolean existsOverlappingDoctorAppointment(@Param("doctorId") Long doctorId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime,
                                               @Param("activeStatuses") List<AppointmentStatus> activeStatuses);

    long countByStatus(AppointmentStatus status);

    @Query("""
            SELECT COALESCE(SUM(dp.consultationFee), 0)
            FROM Appointment a
            JOIN DoctorProfile dp ON dp.user.id = a.doctor.id
            WHERE a.status = :status
            """)
    long sumRevenueByCompletedStatus(@Param("status") AppointmentStatus status);

    @Query("""
            SELECT a.doctor.id
            FROM Appointment a
            WHERE a.status = :status
            GROUP BY a.doctor.id
            ORDER BY COUNT(a.id) DESC
            """)
    List<Long> findDoctorIdsByCompletedCountDesc(@Param("status") AppointmentStatus status);
}
