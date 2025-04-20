package com.refugeeintegration.backend.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.refugeeintegration.backend.entity.SimulationMetadata;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class SimulationMetadataRepository {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Key = simulationId (String), Value = metadata
    @Getter
    private Map<String, SimulationMetadata> simulations = new HashMap<>();

    @PostConstruct
    public void loadSimulations() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:simulation/*.json");

            Map<String, SimulationMetadata> loaded = new HashMap<>();

            for (Resource resource : resources) {
                try (InputStream inputStream = resource.getInputStream()) {
                    SimulationMetadata metadata = objectMapper.readValue(inputStream, SimulationMetadata.class);
                    loaded.put(metadata.getSimulationId(), metadata);
                    log.info("✅ Loaded simulation: {}", metadata.getSimulationId());
                }
            }

            this.simulations = loaded;
            log.info("✅ Total loaded simulation metadata: {}", simulations.size());
        } catch (Exception e) {
            log.error("❌ Failed to load simulation metadata", e);
            this.simulations = Collections.emptyMap();
        }
    }

    public SimulationMetadata getSimulationMetadata(String simulationId) {
        return simulations.get(simulationId);
    }

    public Map<String, SimulationMetadata> getAll() {
        return simulations;
    }
    public String getInitialPrompt(String simulationId) {
        SimulationMetadata metadata = simulations.get(simulationId);
        if (metadata == null) {
            throw new IllegalArgumentException("No simulation metadata found for ID: " + simulationId);
        }
        return metadata.getInitialPrompt(); // assuming there's a `prompt` field
    }
}