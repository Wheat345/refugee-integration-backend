package com.refugeeintegration.backend.repository;

import com.refugeeintegration.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {}
