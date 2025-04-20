package com.refugeeintegration.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Simulation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long simulationId;

    private String simulationName;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
