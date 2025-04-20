package com.refugeeintegration.backend.dto;

import lombok.Data;

@Data
public class UserSimulationUpdateRequest {
    private Long userId;
    private Long simulationId;
    private int newScore;
    private boolean completed;
}