package com.refugeeintegration.backend.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.refugeeintegration.backend.client.OpenAIClient;
import com.refugeeintegration.backend.client.PineconeClient;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PineconeService {
    private final PineconeClient pineconeClient;


    private static final String PINECONE_QUERY_ENDPOINT = "https://refugee-integration-docs-820bisa.svc.aped-4627-b74a.pinecone.io/query";
    private static final String PINECONE_API_KEY = "pcsk_3YyCsG_5KvEq4LdFPffV7HwSfnvRx1quEFKWx6AF9GmU7sYxU2jt1ghFovFcVDCsBgUd8y";
    private static final String PINECONE_NAMESPACE = "interview-questions-answers"; // permit-in-canada, interview-questions-answers
    private static double THRESHOLD = 0.85; // Set a minimum similarity threshold

//    public void upsertData(SomeDataRequest request) {
//        pineconeClient.upsert(request);
//    }

    public void uploadToPinecone(String[] data) {
        // Reuse logic from PineconeUploader
    }
    public static String queryPinecone(String queryText) {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        try {
            // Get the embedding as a float array
            //float[] queryEmbedding = OpenAIClient.getEmbedding(queryText);
            // Ensure vector size matches index dimensions
            float[] queryEmbedding = OpenAIClient.getEmbedding(queryText);
            if (queryEmbedding.length > 1024) {
                queryEmbedding = Arrays.copyOf(queryEmbedding, 1024);
            }
            if (queryEmbedding == null || queryEmbedding.length == 0) {
                throw new IllegalStateException("Failed to get embedding for query: " + queryText);
            }

            // Convert float[] to JsonArray
            JsonArray embeddingArray = new JsonArray();
            for (float value : queryEmbedding) {
                embeddingArray.add(value);
            }
            //System.out.println("Vector size: " + queryEmbedding.length);
            // Construct JSON request
            JsonObject queryObject = new JsonObject();
            queryObject.add("vector", embeddingArray);
            queryObject.addProperty("topK", 10);
            queryObject.addProperty("includeMetadata", true);
            //point to right namespace
            queryObject.addProperty("namespace", PINECONE_NAMESPACE);
            //System.out.println("Using namespace: " + PINECONE_NAMESPACE);
            queryObject.addProperty("includeMetadata", true);
            queryObject.addProperty("includeValues", true);

            // Send request to Pinecone
            RequestBody body = RequestBody.create(queryObject.toString(), MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(PINECONE_QUERY_ENDPOINT)
                    .post(body)
                    .addHeader("Api-Key", PINECONE_API_KEY)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.out.println("Query failed: " + response);
                    return "No relevant data found.";
                }

                JsonObject responseBody = gson.fromJson(response.body().string(), JsonObject.class);
                //System.out.println("rsponseBody: "+ responseBody.toString());

                JsonArray matches = responseBody.getAsJsonArray("matches");

                if (matches == null || matches.size() == 0) {
                    return "No relevant results found.";
                }
                //if(matches.sc)

                // Get the highest score match
                JsonObject topMatch = matches.get(0).getAsJsonObject();

                //debug
                String score = topMatch.get("score").toString();
                //System.out.println("current vector score is "+ score);
                //if score lower than threshold, return
                if (Double.parseDouble(score) < THRESHOLD) {
                    return "Sorry, no relevant information found.";
                }
                // Ensure metadata exists before extracting "text"
                JsonObject metadata = topMatch.has("metadata") ? topMatch.getAsJsonObject("metadata") : null;
                //System.out.println("meta data is "+metadata.toString());
                //String retrievedQuestion = (metadata != null && metadata.has("question")) ? metadata.get("question").getAsString() : "No relevant question found.";
                String retrievedAnswer = (metadata != null && metadata.has("answer")) ? metadata.get("answer").getAsString() : "No relevant answer found.";

                //System.out.println("Retrieved Question: " + retrievedQuestion);
                //System.out.println("Retrieved Answer: " + retrievedAnswer);

                return retrievedAnswer;  // Return the answer instead of the question
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing query.";
        }
    }
}