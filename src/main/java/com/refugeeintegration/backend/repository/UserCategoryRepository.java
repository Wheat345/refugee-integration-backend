package com.refugeeintegration.backend.repository;

import com.refugeeintegration.backend.entity.Category;
import com.refugeeintegration.backend.entity.User;
import com.refugeeintegration.backend.entity.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {
    List<UserCategory> findByUser(User user);
    Optional<UserCategory> findByUserAndCategory(User user, Category category);
}
