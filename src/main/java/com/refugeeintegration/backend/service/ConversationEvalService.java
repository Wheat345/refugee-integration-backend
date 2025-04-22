package com.refugeeintegration.backend.service;


import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationEvalService {
    private final OkHttpClient client = new OkHttpClient();
    private static final String OPENAI_API_KEY = "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";
    private static final String OPENAI_URL = "https://api.openai.com/v1/embeddings";
    private static final String OPENAI_CHAT_URL = "https://api.openai.com/v1/chat/completions";

    public String evaluateConversation(List<Map<String, String>> turns) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gpt-3.5-turbo");
        log.info("test_test_3");

        JsonArray messages = new JsonArray();
        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        //systemMsg.addProperty("content", "You are a language evaluator. Evaluate the full exchange in a realistic English conversation simulation. Evaluate the USER's responses across the following four criteria: 1. Completeness – Did the user answer fully and appropriately? 2. Politeness – Did the user respond in a respectful and polite manner? 3. Grammar and Fluency – Is the response grammatically correct and fluent? 4. Detail – Is the answer sufficiently detailed for a real-life situation? Assign a final score from 0 to 100. The score should reflect overall communication quality based on the four criteria above. Format your response like this: Score: XX/100 Follow with 2–3 lines of feedback that includes specific strengths and areas for improvement. Be strict and realistic. Do not be overly generous. Penalize vague, incomplete, or impolite responses.");
        //systemMsg.addProperty("content", "You are a conversation evaluator. Evaluate the full exchange and return a final score from 0 to 100 with feedback. Start with 'Score: XX/100'.");

        systemMsg.addProperty("content",
                "You are a language evaluator. Your task is to assess ONLY the user's responses in a realistic English conversation simulation.\n" +
                        "\n" +
                        "The assistant's messages are prompts and should NOT be evaluated.\n" +
                        "\n" +
                        "Evaluate the USER's responses across the following four criteria:\n" +
                        "1. **Completeness** – Did the user answer fully and appropriately?\n" +
                        "2. **Politeness** – Did the user respond in a respectful and polite manner?\n" +
                        "3. **Grammar and Fluency** – Is the response grammatically correct and fluent?\n" +
                        "4. **Detail** – Is the answer sufficiently detailed for a real-life situation?\n" +
                        "\n" +
                        "Assign a final score from 0 to 100. The score should reflect overall communication quality based on the four criteria above.\n" +
                        "\n" +
                        "**Format your response like this:**\n" +
                        "**Score: XX/100**\n" +
                        "Follow with 2–3 lines of feedback that includes specific strengths and areas for improvement.\n" +
                        "\n" +
                        "Be strict and realistic. Do not be overly generous. Penalize vague, incomplete, or impolite responses."
        );

        messages.add(systemMsg);
        //test
        for (Map<String, String> turn : turns) {
            log.info("Role: " + turn.get("role"));
            log.info("Content: " + turn.get("content"));
            log.info("--------------------------");
        }
        log.info("test_test_4"+turns.size());
        //end test
        for (Map<String, String> turn : turns) {
            JsonObject obj = new JsonObject();
            obj.addProperty("role", turn.get("role"));
            obj.addProperty("content", turn.get("content"));
            messages.add(obj);
        }

        requestBody.add("messages", messages);
        requestBody.addProperty("temperature", 0.2);


        Request request = new Request.Builder()
                .url(OPENAI_CHAT_URL)
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Error: " + response);
            JsonObject jsonResponse = JsonParser.parseString(response.body().string()).getAsJsonObject();
            return jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject()
                    .getAsJsonObject("message").get("content").getAsString();
        }
    }
}