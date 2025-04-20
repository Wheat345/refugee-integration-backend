package com.refugeeintegration.backend.repository;

import com.refugeeintegration.backend.entity.Simulation;
import com.refugeeintegration.backend.entity.User;
import com.refugeeintegration.backend.entity.UserSimulation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSimulationRepository extends JpaRepository<UserSimulation, Long> {
    List<UserSimulation> findByUser(User user);
    Optional<UserSimulation> findByUserAndSimulation(User user, Simulation simulation);

}
