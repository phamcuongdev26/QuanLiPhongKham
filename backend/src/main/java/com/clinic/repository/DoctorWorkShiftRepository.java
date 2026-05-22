package com.clinic.repository;

import com.clinic.entity.DoctorWorkShift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorWorkShiftRepository extends JpaRepository<DoctorWorkShift, Long> {
    List<DoctorWorkShift> findByDoctor_IdAndIsActiveTrue(Long doctorId);
}

