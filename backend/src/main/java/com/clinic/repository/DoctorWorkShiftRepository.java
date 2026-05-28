package com.clinic.repository;

import com.clinic.entity.DoctorWorkShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface DoctorWorkShiftRepository extends JpaRepository<DoctorWorkShift, Long> {
    List<DoctorWorkShift> findByDoctor_IdAndIsActiveTrue(Long doctorId);
    List<DoctorWorkShift> findByDoctor_IdAndDayOfWeekAndIsActiveTrueOrderByStartTimeAsc(Long doctorId, DayOfWeek dayOfWeek);

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
