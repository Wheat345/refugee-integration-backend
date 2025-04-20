package com.refugeeintegration.backend.service;

import com.refugeeintegration.backend.dto.UserSimulationUpdateRequest;
import com.refugeeintegration.backend.entity.Simulation;
import com.refugeeintegration.backend.entity.User;
import com.refugeeintegration.backend.entity.UserSimulation;
import com.refugeeintegration.backend.repository.SimulationRepository;
import com.refugeeintegration.backend.repository.UserRepository;
import com.refugeeintegration.backend.repository.UserSimulationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSimulationService {

    private final UserSimulationRepository userSimulationRepository;
    private final UserRepository userRepository;
    private final SimulationRepository simulationRepository;

    public void updateUserSimulation(UserSimulationUpdateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Simulation simulation = simulationRepository.findById(request.getSimulationId())
                .orElseThrow(() -> new IllegalArgumentException("Simulation not found"));

        UserSimulation userSimulation = userSimulationRepository.findByUserAndSimulation(user, simulation)
                .orElseThrow(() -> new IllegalArgumentException("Simulation not found for user"));

        userSimulation.setCompleted(request.isCompleted());

        if (request.getNewScore() > userSimulation.getHighestScore()) {
            userSimulation.setHighestScore(request.getNewScore());
        }

        userSimulationRepository.save(userSimulation);
    }
}