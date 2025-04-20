package com.refugeeintegration.backend.repository;

import com.refugeeintegration.backend.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyRepository extends JpaRepository<Family, Long> {}
