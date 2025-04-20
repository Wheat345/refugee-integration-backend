
package com.refugeeintegration.backend.service;

import com.refugeeintegration.backend.entity.*;
import com.refugeeintegration.backend.model.*;
import com.refugeeintegration.backend.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {


    @PersistenceContext
    private EntityManager entityManager;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SimulationRepository simulationRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final UserSimulationRepository userSimulationRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return buildLoginResponse(user);
    }

    @Transactional
    public SignUpResponse signup(SignUpRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists with email: " + request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setBirthday(request.getBirthday());
        user.setGender(request.getGender());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreateDate(LocalDateTime.now());

        User savedUser = userRepository.saveAndFlush(user); // ✅ 强制获取主键

        // Assign default categories and simulations
        List<Category> categories = categoryRepository.findAll();
        for (Category cat : categories) {
            UserCategory uc = new UserCategory();
            uc.setUser(savedUser); // ✅ 保证 user_id 存在
            uc.setCategory(cat);
            uc.setJoinedAt(LocalDateTime.now());
            userCategoryRepository.save(uc);

            List<Simulation> sims = simulationRepository.findByCategory(cat);
            for (Simulation sim : sims) {
                UserSimulation us = new UserSimulation();
                us.setUser(savedUser);
                us.setSimulation(sim);
                us.setHighestScore(null);
                us.setCompleted(false);
                us.setLastPracticedAt(null);
                userSimulationRepository.save(us);
            }
        }
        //return new SignUpResponse(savedUser.getUserId(), "Signup successful");

        SignUpResponse response = new SignUpResponse();
        response.setUserId(user.getUserId());
        response.setName(user.getName());
        return response;
    }

    private LoginResponse buildLoginResponse(User user) {
        List<UserCategory> userCategories = userCategoryRepository.findByUser(user);
        List<LoginResponse.CategoryProgress> progressList = new ArrayList<>();

        for (UserCategory uc : userCategories) {
            Category category = uc.getCategory();
            List<Simulation> sims = simulationRepository.findByCategory_CategoryId(category.getCategoryId());

            int total = sims.size();
            int completed = 0;
            List<LoginResponse.CategoryProgress.SimulationProgress> simProgressList = new ArrayList<>();

            for (Simulation sim : sims) {
                UserSimulation us = userSimulationRepository.findByUserAndSimulation(user, sim).orElse(null);
                boolean isCompleted = us != null && Boolean.TRUE.equals(us.getCompleted());
                Integer score = us != null ? us.getHighestScore() : null;

                if (isCompleted) {
                    completed++;
                }

                // Add simulation details
                LoginResponse.CategoryProgress.SimulationProgress sp = new LoginResponse.CategoryProgress.SimulationProgress();
                sp.setSimulationId(sim.getSimulationId());
                sp.setSimulationName(sim.getSimulationName());
                sp.setHighestScore(score);
                sp.setCompleted(isCompleted);

                simProgressList.add(sp);
            }

            LoginResponse.CategoryProgress cp = new LoginResponse.CategoryProgress();
            cp.setCategoryId(category.getCategoryId());
            cp.setCategoryName(category.getName());
            cp.setCompletedSimulations(completed);
            cp.setTotalSimulations(total);
            cp.setCompleted(completed == total);
            cp.setSimulations(simProgressList);

            progressList.add(cp);
        }

        LoginResponse response = new LoginResponse();
        response.setUserId(user.getUserId());
        response.setName(user.getName());
        response.setCategories(progressList);
        return response;
    }
}
