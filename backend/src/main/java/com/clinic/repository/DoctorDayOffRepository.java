package com.clinic.repository;

import com.clinic.entity.DoctorDayOff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DoctorDayOffRepository extends JpaRepository<DoctorDayOff, Long> {
    boolean existsByDoctor_IdAndDayOff(Long doctorId, LocalDate dayOff);
    List<DoctorDayOff> findByDoctor_Id(Long doctorId);
}

