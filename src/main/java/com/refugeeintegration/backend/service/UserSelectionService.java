package com.refugeeintegration.backend.service;

import com.refugeeintegration.backend.entity.*;
import com.refugeeintegration.backend.model.UserSelectionRequest;
import com.refugeeintegration.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserSelectionService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SimulationRepository simulationRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final UserSimulationRepository userSimulationRepository;

    public void updateUserSelection(UserSelectionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            userCategoryRepository.findByUserAndCategory(user, category)
                    .orElseGet(() -> {
                        UserCategory uc = new UserCategory();
                        uc.setUser(user);
                        uc.setCategory(category);
                        uc.setJoinedAt(LocalDateTime.now());
                        return userCategoryRepository.save(uc);
                    });
        }

        if (request.getSimulationId() != null) {
            Simulation simulation = simulationRepository.findById(request.getSimulationId())
                    .orElseThrow(() -> new RuntimeException("Simulation not found"));

            UserSimulation us = userSimulationRepository.findByUserAndSimulation(user, simulation)
                    .orElseGet(() -> {
                        UserSimulation newSim = new UserSimulation();
                        newSim.setUser(user);
                        newSim.setSimulation(simulation);
                        newSim.setLastPracticedAt(LocalDateTime.now());
                        return newSim;
                    });

            if (request.getScore() != null) {
                Integer prevScore = us.getHighestScore() != null ? us.getHighestScore() : 0;
                us.setHighestScore(Math.max(prevScore, request.getScore()));
            }

            if (request.getCompleted() != null) {
                us.setCompleted(request.getCompleted());
            }

            us.setLastPracticedAt(LocalDateTime.now());
            userSimulationRepository.save(us);
        }
    }
}
