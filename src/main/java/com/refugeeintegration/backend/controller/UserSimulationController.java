package com.refugeeintegration.backend.controller;

import com.refugeeintegration.backend.dto.UserSimulationUpdateRequest;
import com.refugeeintegration.backend.service.UserSimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-simulation")
@RequiredArgsConstructor
public class UserSimulationController {

    private final UserSimulationService userSimulationService;

    @PostMapping("/update")
    public ResponseEntity<String> updateUserSimulation(@RequestBody UserSimulationUpdateRequest request) {
        userSimulationService.updateUserSimulation(request);
        return ResponseEntity.ok("✅ UserSimulation updated successfully");
    }
}