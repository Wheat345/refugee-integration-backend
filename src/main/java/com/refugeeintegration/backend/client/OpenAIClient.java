package com.refugeeintegration.backend.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Response;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OpenAIClient {
    private static final String OPENAI_API_KEY = "mask";
    private static final String OPENAI_URL = "https://api.openai.com/v1/embeddings";
    private static final String OPENAI_CHAT_URL = "https://api.openai.com/v1/chat/completions";
    private static final OkHttpClient client = new OkHttpClient();  // Reuse HTTP client for performance

    /**
     * Generates an embedding for the provided text using OpenAI's API.
     *
     * @param text The text to embed.
     * @return A float array representing the embedding.
     * @throws IOException if there's an issue with the API request.
     */
    public static float[] getEmbedding(String text) throws IOException {
        // Create JSON request body
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("input", text);
        requestBody.addProperty("model", "text-embedding-ada-002");

        // Build HTTP request
        Request request = new Request.Builder()
                .url(OPENAI_URL)
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                .build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response.code() + ": " + response.message());
            }

            // Parse the response to extract the embedding
            String responseBody = response.body().string();
            return parseEmbedding(responseBody);
        }
    }

    /**
     * Parses the embedding response from OpenAI and returns it as a float array.
     *
     * @param responseBody The raw JSON response from OpenAI.
     * @return A float array representing the embedding.
     */
    private static float[] parseEmbedding(String responseBody) {
        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
        JsonArray embeddingArray = jsonResponse
                .getAsJsonArray("data")
                .get(0)
                .getAsJsonObject()
                .getAsJsonArray("embedding");

        // Convert JSON array to float array
        float[] embedding = new float[embeddingArray.size()];
        for (int i = 0; i < embeddingArray.size(); i++) {
            embedding[i] = embeddingArray.get(i).getAsFloat();
        }
        return embedding;
    }


    /**
     * Generates an answer using OpenAI's GPT model with context from Pinecone.
     * @param question The user question.
     * @param context  The retrieved context from Pinecone.
     * @return The AI-generated response.
     */
    public static String[] answerWithContext(String question, String context) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gpt-3.5-turbo");  // You can change this to gpt-4 or gpt-3.5-turbo
        JsonArray messages = new JsonArray();

        // System Prompt
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", "You are an assistant helping users with immigration-related questions. Use the provided context to answer accurately.");
        messages.add(systemMessage);

        // Context
        JsonObject contextMessage = new JsonObject();
        contextMessage.addProperty("role", "user");
        contextMessage.addProperty("content", "Here is the relevant context:\n" + context);
        messages.add(contextMessage);

        // User Question
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", question);
        messages.add(userMessage);

        requestBody.add("messages", messages);
        requestBody.addProperty("temperature", 0.2);

        Request request = new Request.Builder()
                .url(OPENAI_CHAT_URL)
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error: " + response.code() + " - " + response.message());
            }

            String responseBody = response.body().string();
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

            String feedback = jsonResponse
                    .getAsJsonArray("choices")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content")
                    .getAsString();
            //int score = extractScore(feedback);

            // Print feedback
            //System.out.println("AI Score from rag and openai api: " + score + "/100");
            //System.out.println("AI Feedback: " + feedback);
            return new String[]{feedback,String.valueOf(0)};
//            return jsonResponse
//                    .getAsJsonArray("choices")
//                    .get(0)
//                    .getAsJsonObject()
//                    .getAsJsonObject("message")
//                    .get("content")
//                    .getAsString();
        }
    }

    /**
     * Compares the user’s answer with the predefined ideal answer and assigns a score.
     *
     * @param userAnswer  The user's response.
     * @param idealAnswer The ideal response retrieved from Pinecone.
     * @return Score from 0-100 and improvement feedback.
     * @throws IOException if API request fails.
     */
    public static String evaluateResponse(String userAnswer, String idealAnswer) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gpt-3.5-turbo");
        String[] ans = new String[2];
        JsonArray messages = new JsonArray();

        // System Prompt (What the AI should do)
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        //systemMessage.addProperty("content", "You are an interview evaluator. Compare the user's answer with the ideal answer and provide a score from 0 to 100. Also, give improvement suggestions.");
        systemMessage.addProperty("content", "You are a career assistant and evaluator. \n" +
                "\n" +
                "When the user submits a response to a practice question:\n" +
                "\n" +
                "1. First, output a line that starts with: **Score: [number]/100**\n" +
                "2. The user may either practice interview questions or ask related career questions. If the user asks a question, respond with helpful and accurate advice.\n" +
                "3. Then write 2–3 lines of feedback with suggestions for improvement.\n" +
                "4. Always begin with the score line, no matter what.\n" +
                "\n" +
                "Do not skip the score. If you are unsure, estimate based on clarity, completeness, and relevance.");

        messages.add(systemMessage);
//You are a career assistant. The user may either practice interview questions or ask related career questions. If the user asks a question, respond with helpful and accurate advice.
        // Ideal Answer
        JsonObject idealAnswerMessage = new JsonObject();
        idealAnswerMessage.addProperty("role", "assistant");
        idealAnswerMessage.addProperty("content", "Ideal Answer: " + idealAnswer);
        messages.add(idealAnswerMessage);

        // User Answer
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", "User's Answer: " + userAnswer);
        messages.add(userMessage);

        requestBody.add("messages", messages);
        requestBody.addProperty("temperature", 0.2);

        Request request = new Request.Builder()
                .url(OPENAI_CHAT_URL)
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error: " + response.code() + " - " + response.message());
            }

            String responseBody = response.body().string();
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

            // Extract the AI response
            String content = jsonResponse
                    .getAsJsonArray("choices")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content")
                    .getAsString();

            //System.out.println("!!!!!!!!!!!!!!!!!! "+jsonResponse.toString());


            // Extract numeric score from AI feedback (Example: "Your score is 75 out of 100.")
            String score = extractScore(content,"Score");
            String feedback = extractFeedback(content);
            //System.out.println("socre is "+score);
//
//            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//            System.out.println("contents jjjjjjjjj is "+content);
//            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//            System.out.println("feedback     fffff is "+feedback);
//            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

            // Construct JSON response
            JsonObject resultJson = new JsonObject();
            resultJson.addProperty("score", score);
            resultJson.addProperty("feedback", feedback);

            return resultJson.toString(); // Return JSON string

            //ans[0] = score;
            //ans[1] = feedback;

            //return ans;
        }
    }

    private static String extractScore(String feedback, String name) {
        int score = 0; // Default score if not found
        String[] lines = feedback.split("\n"); // Split by new lines
        for (String line : lines) {
            //System.out.println("grgffffrgr "+line);
            if (line.trim().startsWith(name)) { // Look for the "Score:" prefix
                String[] parts = line.split(":");
                if (parts.length > 1) {
                    try {
                        if (parts[1].contains("/")) {
                            return parts[1].split("/")[0].trim();
                        }
                        return parts[1].trim();
                        //score = Integer.parseInt(parts[1].trim()); // Extract and convert to int
                        //return score; // Return the extracted score
                    } catch (NumberFormatException ignored) {
                        //System.err.println("Invalid score format in feedback: " + line);
                    }
                }
            }
        }
        return ""; // Return 0 if no valid score is found
    }

    private static String extractFeedback(String content) {
        // Split content by new lines
        String[] lines = content.split("\n");
        StringBuilder feedback = new StringBuilder();
        boolean startAppending = false;

        for (String line : lines) {
            line = line.trim();

            // Skip the "Score: xx/100" line and the next empty line
            if (line.startsWith("Score:")) {
                startAppending = true; // Indicate the next lines might need to be skipped
                continue;
            }

            if (startAppending && line.isEmpty()) {
                startAppending = false; // Skip this empty line after the score
                continue;
            }

            // Append the remaining lines
            feedback.append(line).append("\n");
        }

        return feedback.toString().trim(); // Return cleaned feedback
    }

    public static String[] askQuestion(String question, String userAnswer){

        return new String[]{"aa","bb"};


    }


}
