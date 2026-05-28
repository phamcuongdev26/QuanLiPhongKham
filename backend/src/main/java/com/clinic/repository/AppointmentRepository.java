package com.clinic.repository;

import com.clinic.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    interface DashboardStatsProjection {
        long getTotalAppointments();
        long getPendingAppointments();
        long getConfirmedAppointments();
        long getCompletedAppointments();
        long getTotalDoctors();
        long getTotalPatients();
        long getRevenue();
    }

    interface BusiestDoctorProjection {
        Long getId();
        String getFullName();
        String getEmail();
        String getSpecialtyName();
    }

    @Query(value = "SELECT * FROM appointments WHERE patient_id = :patientId ORDER BY start_time DESC", nativeQuery = true)
    List<Appointment> findByPatientId(@Param("patientId") Long patientId);

    @Query(value = "SELECT * FROM appointments WHERE doctor_id = :doctorId ORDER BY start_time DESC", nativeQuery = true)
    List<Appointment> findByDoctorId(@Param("doctorId") Long doctorId);

    @Query(value = "SELECT * FROM appointments WHERE doctor_id = :doctorId AND start_time BETWEEN :from AND :to ORDER BY start_time ASC", nativeQuery = true)
    List<Appointment> findByDoctorIdAndStartTimeBetween(@Param("doctorId") Long doctorId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

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

    @Query(value = """
            SELECT
                (SELECT COUNT(*) FROM appointments)                                                        AS total_appointments,
                (SELECT COUNT(*) FROM appointments WHERE status = 'PENDING')                              AS pending_appointments,
                (SELECT COUNT(*) FROM appointments WHERE status = 'CONFIRMED')                            AS confirmed_appointments,
                (SELECT COUNT(*) FROM appointments WHERE status = 'COMPLETED')                            AS completed_appointments,
                (SELECT COUNT(*) FROM users WHERE role = 'DOCTOR')                                        AS total_doctors,
                (SELECT COUNT(*) FROM users WHERE role = 'PATIENT')                                       AS total_patients,
                (SELECT COALESCE(SUM(dp.consultation_fee), 0)
                 FROM appointments a JOIN doctor_profiles dp ON dp.user_id = a.doctor_id
                 WHERE a.status = 'COMPLETED')                                                            AS revenue
            """, nativeQuery = true)
    DashboardStatsProjection findDashboardStats();

    @Query(value = """
            SELECT u.id, u.full_name AS fullName, u.email, s.name AS specialtyName
            FROM appointments a
            JOIN users u ON u.id = a.doctor_id
            JOIN doctor_profiles dp ON dp.user_id = a.doctor_id
            LEFT JOIN specialties s ON s.id = dp.specialty_id
            WHERE a.status = 'COMPLETED'
            GROUP BY u.id, u.full_name, u.email, s.name
            ORDER BY COUNT(a.id) DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<BusiestDoctorProjection> findBusiestDoctor();

    @Query(value = "SELECT a FROM Appointment a " +
                   "JOIN FETCH a.patient " +
                   "JOIN FETCH a.doctor " +
                   "LEFT JOIN FETCH a.specialty",
           countQuery = "SELECT COUNT(a) FROM Appointment a")
    Page<Appointment> findAllWithDetails(Pageable pageable);
}
