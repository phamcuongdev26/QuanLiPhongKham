package com.clinic.repository;

import com.clinic.entity.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {
    List<DoctorProfile> findBySpecialty_Id(Long specialtyId);

    interface DoctorSummaryProjection {
        Long getId();
        String getFullName();
        String getEmail();
        Long getSpecialtyId();
        String getSpecialtyName();
        Long getConsultationFee();
        String getTitle();
        String getBio();
        Boolean getIsActive();
    }

    @Query(value = """
            SELECT
                u.id,
                u.full_name AS fullName,
                u.email,
                s.id AS specialtyId,
                s.name AS specialtyName,
                dp.consultation_fee AS consultationFee,
                dp.title,
                dp.bio,
                u.is_active AS isActive
            FROM doctor_profiles dp
            JOIN users u ON u.id = dp.user_id
            LEFT JOIN specialties s ON s.id = dp.specialty_id
            ORDER BY u.full_name ASC
            """, nativeQuery = true)
    List<DoctorSummaryProjection> findAllDoctorSummaries();

    @Query(value = """
            SELECT
                u.id,
                u.full_name AS fullName,
                u.email,
                s.id AS specialtyId,
                s.name AS specialtyName,
                dp.consultation_fee AS consultationFee,
                dp.title,
                dp.bio,
                u.is_active AS isActive
            FROM doctor_profiles dp
            JOIN users u ON u.id = dp.user_id
            LEFT JOIN specialties s ON s.id = dp.specialty_id
            WHERE dp.specialty_id = :specialtyId
            ORDER BY u.full_name ASC
            """, nativeQuery = true)
    List<DoctorSummaryProjection> findDoctorSummariesBySpecialty(@Param("specialtyId") Long specialtyId);

    @Query(value = """
            SELECT
                u.id,
                u.full_name AS fullName,
                u.email,
                s.id AS specialtyId,
                s.name AS specialtyName,
                dp.consultation_fee AS consultationFee,
                dp.title,
                dp.bio,
                u.is_active AS isActive
            FROM doctor_profiles dp
            JOIN users u ON u.id = dp.user_id
            LEFT JOIN specialties s ON s.id = dp.specialty_id
            WHERE dp.user_id = :doctorId
            LIMIT 1
            """, nativeQuery = true)
    Optional<DoctorSummaryProjection> findDoctorSummaryById(@Param("doctorId") Long doctorId);
}
