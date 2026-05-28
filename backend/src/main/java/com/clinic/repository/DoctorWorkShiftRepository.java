package com.clinic.repository;

import com.clinic.entity.DoctorWorkShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface DoctorWorkShiftRepository extends JpaRepository<DoctorWorkShift, Long> {
    @Query(value = """
            SELECT *
            FROM doctor_work_shifts
            WHERE doctor_id = :doctorId AND is_active = true
            ORDER BY day_of_week ASC, start_time ASC
            """, nativeQuery = true)
    List<DoctorWorkShift> findActiveByDoctorId(@Param("doctorId") Long doctorId);

    @Query(value = """
            SELECT *
            FROM doctor_work_shifts
            WHERE doctor_id = :doctorId
              AND day_of_week = :dayOfWeek
              AND is_active = true
            ORDER BY start_time ASC
            """, nativeQuery = true)
    List<DoctorWorkShift> findActiveByDoctorIdAndDayOfWeek(@Param("doctorId") Long doctorId,
                                                           @Param("dayOfWeek") String dayOfWeek);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM doctor_work_shifts
                WHERE doctor_id = :doctorId
              AND day_of_week = :dayOfWeek
              AND is_active = true
              AND start_time <= :startTime
              AND end_time >= :endTime
            )
            """, nativeQuery = true)
    int existsShiftCovering(@Param("doctorId") Long doctorId,
                            @Param("dayOfWeek") String dayOfWeek,
                            @Param("startTime") LocalTime startTime,
                            @Param("endTime") LocalTime endTime);
}
