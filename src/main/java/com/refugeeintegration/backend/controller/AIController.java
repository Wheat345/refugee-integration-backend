package com.refugeeintegration.backend.controller;

import com.refugeeintegration.backend.dto.ConversationReplyRequest;
import com.refugeeintegration.backend.service.AIService;
import com.refugeeintegration.backend.service.PineconeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {
    private final AIService aiService;

    @PostMapping("/uploadQuestion")
    public ResponseEntity<Void> uploadQuestion(@RequestBody String question, String userAnswer) {
        aiService.uploadInterviewQuestion(question,userAnswer);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/agentFeedback")
    public ResponseEntity<String> agentFeedback(@RequestParam String question,@RequestParam String userAnswer) throws IOException {
        // Optionally, you could pass these to your AIService if logic exists.
        String idealAnswer = PineconeService.queryPinecone(question);
        String feedback = aiService.getAgentFeedback(userAnswer,idealAnswer);//.replaceAll("(?m)^\\s*$[\n\r]+", "");;
        System.out.println("question "+ question);
        System.out.println("userAnswer "+ userAnswer);
        System.out.println("idealAnswer "+ idealAnswer);
        System.out.println("feedback "+ feedback);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/conversation/start")
    public ResponseEntity<Map<String, Object>> startSimulation(@RequestParam String simulationId) {
        return ResponseEntity.ok(aiService.startConversation(simulationId));
    }

    @PostMapping("/conversation/reply")
    public ResponseEntity<Map<String, Object>> replyToSimulation(@RequestBody ConversationReplyRequest request) {
        return ResponseEntity.ok(
                aiService.replyToConversation(request.getConversationId(), request.getUserMessage())
        );
    }

    @GetMapping("/conversation/evaluate")
    public ResponseEntity<Map<String, Object>> evaluateConversation(@RequestParam String conversationId) throws IOException {
        return ResponseEntity.ok(aiService.evaluateConversation(conversationId));
    }
}