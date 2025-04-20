package com.refugeeintegration.backend.model;

import lombok.Data;

@Data
public class UserSelectionRequest {
    private Long userId;
    private Long categoryId;
    private Long simulationId;
    private Integer score; // optional
    private Boolean completed; // optional
}
