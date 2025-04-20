package com.refugeeintegration.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usersimulation")  // <-- ✅ MUST match your new table name
@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserSimulation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userSimulationId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "simulation_id")
    private Simulation simulation;

    @Column(name = "highest_score")
    private Integer highestScore;

    @Column(name = "completed")
    private Boolean completed;

    @Column(name = "last_practiced_at")
    private LocalDateTime lastPracticedAt;


}
