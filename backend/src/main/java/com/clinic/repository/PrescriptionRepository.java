package com.clinic.repository;

import com.clinic.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    @Query("""
            SELECT DISTINCT p FROM Prescription p
            LEFT JOIN FETCH p.items
            WHERE p.id IN :ids
            """)
    List<Prescription> findByIdInWithItems(@Param("ids") List<Long> ids);
}

