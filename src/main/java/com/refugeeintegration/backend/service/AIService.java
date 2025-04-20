package com.refugeeintegration.backend.service;

import com.refugeeintegration.backend.client.OpenAIClient;
import com.refugeeintegration.backend.repository.SimulationMetadataRepository;
import com.refugeeintegration.backend.repository.SimulationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AIService {
    private final OpenAIClient openAIClient;
    private final PineconeService pineconeService;
    private final SimulationMetadataRepository simulationMetadataRepository;
    private final ConversationService conversationService;
    private final ConversationEvalService conversationEvalService;

    public void uploadInterviewQuestion(String question, String userAnswer) {
        String response[] = openAIClient.askQuestion(question,userAnswer);
        pineconeService.uploadToPinecone(response);
    }

    public String getAgentFeedback(String userAnswer, String idealAnswer) throws IOException {
        // Call OpenAIClient to get feedback directly
        String response = openAIClient.evaluateResponse(userAnswer, idealAnswer);
        return response;
        // For now, you can just join the response into a single feedback string.
        // This can be enhanced later if needed.
        //System.out.println("res "+ Arrays.toString(response));
        //return String.join("\n", response);
    }

    public Map<String, Object> startConversation(String simulationId) {
        String prompt = simulationMetadataRepository.getInitialPrompt(simulationId);
        return conversationService.initialize(simulationId, prompt);
    }

    public Map<String, Object> replyToConversation(String conversationId, String userInput) {
        return conversationService.advance(conversationId, userInput);
    }

    public Map<String, Object> evaluateConversation(String conversationId) throws IOException {
        List<Map<String, String>> turns = conversationService.getConversation(conversationId);
        String feedback = conversationEvalService.evaluateConversation(turns);
        int score = extractScore(feedback);
        System.out.println("test_test");
        System.out.println("score "+ score);
        System.out.println("feedback "+ feedback);
        return Map.of("scoreFeedback", feedback, "completed", true, "score",score);
    }

    public int extractScore(String feedbackText) {
        // 正则：匹配 "Score: XX/100" 或 "**Score: XX/100**"
        Pattern pattern = Pattern.compile("(?i)\\*?\\*?Score[:：]?\\s*(\\d{1,3})\\s*/\\s*100\\*?\\*?");
        Matcher matcher = pattern.matcher(feedbackText);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new IllegalArgumentException("Score not found in feedback.");
    }
}