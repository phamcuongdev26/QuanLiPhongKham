package com.clinic.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prescription_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @Column(name = "drug_name", nullable = false, length = 200)
    private String drugName;

    @Column(name = "dosage", length = 200)
    private String dosage;

    @Column(name = "frequency", length = 200)
    private String frequency;

    @Column(name = "duration", length = 200)
    private String duration;

    @Column(name = "instruction", length = 500)
    private String instruction;
}

