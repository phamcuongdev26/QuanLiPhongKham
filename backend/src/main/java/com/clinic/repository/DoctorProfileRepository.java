package com.clinic.repository;

import com.clinic.entity.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {
    List<DoctorProfile> findBySpecialty_Id(Long specialtyId);
}

