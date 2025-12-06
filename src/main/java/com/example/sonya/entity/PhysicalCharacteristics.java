package com.example.sonya.entity;

import com.example.sonya.enums.BMICategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "physical_characteristics")
public class PhysicalCharacteristics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "bmi", nullable = false)
    private Double bmi;

    @Column(name = "bmr", nullable = false)
    private Double bmr;

    @Enumerated(EnumType.STRING)
    @Column(name = "bmi_category", nullable = false)
    private BMICategory bmiCategory;

    @Column(name = "optimal_weight")
    private Double optimalWeight;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
