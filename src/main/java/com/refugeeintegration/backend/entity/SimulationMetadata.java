package com.refugeeintegration.backend.entity;

import lombok.Data;

import java.util.List;

@Data
public class SimulationMetadata {
    private String simulationId;
    private String title;
    private String type; // e.g., "conversation"
    private String languageLevel;
    private String context;
    private String initialPrompt;
    private String role;
    private List<String> factors;
    private List<String> flow;
    private List<String> triggers;
}