package com.amazonlocker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "compartments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long compartmentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompartmentSize compartmentSize;

    @Column(nullable = false)
    @Builder.Default
    private boolean isOccupied = false;
}
