package com.clinic.repository;

import com.clinic.entity.DoctorDayOff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DoctorDayOffRepository extends JpaRepository<DoctorDayOff, Long> {
    @Query(value = """
            SELECT EXISTS (
                SELECT 1 FROM doctor_days_off
                WHERE doctor_id = :doctorId AND day_off = :dayOff
            )
            """, nativeQuery = true)
    boolean existsByDoctorIdAndDayOff(@Param("doctorId") Long doctorId,
                                      @Param("dayOff") LocalDate dayOff);

    @Query(value = "SELECT * FROM doctor_days_off WHERE doctor_id = :doctorId ORDER BY day_off DESC", nativeQuery = true)
    List<DoctorDayOff> findByDoctorId(@Param("doctorId") Long doctorId);

    @Query(value = "SELECT * FROM doctor_days_off WHERE doctor_id = :doctorId AND day_off = :dayOff LIMIT 1", nativeQuery = true)
    Optional<DoctorDayOff> findByDoctorIdAndDayOff(@Param("doctorId") Long doctorId,
                                                   @Param("dayOff") LocalDate dayOff);
}
