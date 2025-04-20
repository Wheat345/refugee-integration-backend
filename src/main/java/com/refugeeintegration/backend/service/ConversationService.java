package com.refugeeintegration.backend.service;

import com.refugeeintegration.backend.entity.SimulationMetadata;
import com.refugeeintegration.backend.repository.SimulationMetadataRepository;
import com.refugeeintegration.backend.repository.SimulationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {
    private final SimulationMetadataRepository simulationMetadataRepository;
    private final Map<String, List<Map<String, String>>> history = new HashMap<>();
    private final Map<String, Integer> stepIndex = new HashMap<>();
    private final Map<String, String> simulationMap = new HashMap<>();

    public Map<String, Object> initialize(String simulationId,String prompt) {
        String conversationId = UUID.randomUUID().toString();
        List<Map<String, String>> conversation = new ArrayList<>();
        Map<String, String> aiMsg = Map.of("role", "assistant", "content", prompt);
        conversation.add(aiMsg);

        history.put(conversationId, conversation);
        stepIndex.put(conversationId, 0);
        simulationMap.put(conversationId, simulationId);
        log.info("✅ Stored mapping: conversationId={} -> simulationId={}", conversationId, simulationId);
        return Map.of("conversationId", conversationId, "initialPrompt", prompt);
    }

    public Map<String, Object> advance(String conversationId, String userInput) {
        List<Map<String, String>> conv = history.get(conversationId);
        if (conv == null) {
            conv = new ArrayList<>();
            history.put(conversationId, conv); // 别忘了重新 put 回去
        }
        conv.add(Map.of("role", "user", "content", userInput));
        log.info("test_test_2");

        String simulationId = simulationMap.get(conversationId);
        SimulationMetadata metadata = simulationMetadataRepository.getSimulationMetadata(simulationId);
        int idx = stepIndex.getOrDefault(conversationId, 0);


        boolean isCompleted = idx >= metadata.getFlow().size();
        String nextPrompt = isCompleted ? "Thank you, that’s the end of our simulation." : metadata.getFlow().get(idx);

        conv.add(Map.of("role", "assistant", "content", nextPrompt));
        stepIndex.put(conversationId, idx + 1);

        System.out.println("------------------advance--------------------------------");
        for (Map.Entry<String, List<Map<String, String>>> entry : history.entrySet()) {
            String conversation_id = entry.getKey();
            List<Map<String, String>> conversation_test = entry.getValue();

            System.out.println("Conversation ID: " + conversation_id);
            for (Map<String, String> message : conversation_test) {
                for (Map.Entry<String, String> messageEntry : message.entrySet()) {
                    System.out.println("  " + messageEntry.getKey() + ": " + messageEntry.getValue());
                }
            }
            System.out.println("--------------------------------------------------");
        }
        return Map.of(
                "aiMessage", nextPrompt,
                "step", idx + 1,
                "completed", isCompleted
        );
    }

    public List<Map<String, String>> getConversation(String conversationId) {
        System.out.println("------------------getConversation--------------------------------");
        System.out.println("------------------conversationId is "+ conversationId);
        for (Map.Entry<String, List<Map<String, String>>> entry : history.entrySet()) {
            String conversation_id = entry.getKey();
            List<Map<String, String>> conversation_test = entry.getValue();

            System.out.println("Conversation ID: " + conversation_id);
            for (Map<String, String> message : conversation_test) {
                for (Map.Entry<String, String> messageEntry : message.entrySet()) {
                    System.out.println("  " + messageEntry.getKey() + ": " + messageEntry.getValue());
                }
            }
            System.out.println("--------------------------------------------------");
        }
        return history.getOrDefault(conversationId, new ArrayList<>());
    }
}