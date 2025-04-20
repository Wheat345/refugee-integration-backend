package com.refugeeintegration.backend.model;

import lombok.Data;
import java.util.List;

@Data
public class LoginResponse {
    private Long userId;
    private String name;
    private List<CategoryProgress> categories;

    @Data
    public static class CategoryProgress {
        private Long categoryId;
        private String categoryName;
        private int completedSimulations;
        private int totalSimulations;
        private boolean completed;

        private List<SimulationProgress> simulations;

        @Data
        public static class SimulationProgress {
            private Long simulationId;
            private String simulationName;
            private Integer highestScore;
            private boolean completed;
        }
    }
}
