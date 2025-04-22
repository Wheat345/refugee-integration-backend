package com.refugeeintegration.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;

    private LocalDate birthday;

    private String email;

    private String gender;

    private String password;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    private String language;

    @ManyToOne
    @JoinColumn(name = "family_id", nullable = true)
    private Family family;
}
