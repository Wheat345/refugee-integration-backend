package com.refugeeintegration.backend.repository;

import com.refugeeintegration.backend.entity.Category;
import com.refugeeintegration.backend.entity.Simulation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SimulationRepository extends JpaRepository<Simulation, Long> {

    List<Simulation> findByCategory(Category category);  // ✅ 按对象查

    List<Simulation> findByCategory_CategoryId(Long categoryId);  // ✅ 按ID查
}