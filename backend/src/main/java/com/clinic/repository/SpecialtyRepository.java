package com.clinic.repository;

import com.clinic.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    @Query(value = "SELECT * FROM specialties WHERE LOWER(name) = LOWER(:name) LIMIT 1", nativeQuery = true)
    Optional<Specialty> findByName(@Param("name") String name);

    @Query(value = "SELECT * FROM specialties WHERE is_active = true ORDER BY name ASC", nativeQuery = true)
    List<Specialty> findActiveOrderByName();
}
