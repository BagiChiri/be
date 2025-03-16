package com.pos.be.service.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SEOKeywordGenerator {

    private static final String HUGGING_FACE_API_KEY = "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"; // Add your Hugging Face API key here
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tags";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    public static void main(String[] args) {
        if (HUGGING_FACE_API_KEY == null || HUGGING_FACE_API_KEY.isEmpty()) {
            System.err.println("❌ ERROR: Hugging Face API key is missing! Set it using an environment variable.");
            return;
        }

        List<String> prompts = PromptProvider.getPrompts();

        for (String prompt : prompts) {
            processPrompt(prompt.trim());
        }
    }

    private static void processPrompt(String prompt) {
        if (!prompt.isEmpty()) {
            String response = getHuggingFaceResponseWithRetries(prompt);
            if (response != null) {
                saveToDatabase(prompt, response);
            }
        }
    }

    private static String getHuggingFaceResponseWithRetries(String prompt) {
        int maxRetries = 5;
        int retryDelay = 2000; // Start with 2 seconds
        int attempt = 0;

        while (attempt < maxRetries) {
            attempt++;
            try {
                String response = getHuggingFaceResponse(prompt);
                if (response != null) {
                    return response;
                }
            } catch (Exception e) {
                System.err.println("⚠️ ERROR: Hugging Face API request failed. Attempt " + attempt + "/" + maxRetries);
                e.printStackTrace();
            }

            if (attempt < maxRetries) {
                System.out.println("⏳ Retrying in " + (retryDelay / 1000) + " seconds...");
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                retryDelay *= 2; // Exponential backoff (2x delay each retry)
            }
        }
        System.err.println("❌ ERROR: Max retries reached. Skipping this request.");
        return null;
    }

    private static String getHuggingFaceResponse(String prompt) throws Exception {
        URL url = new URL("https://api-inference.huggingface.co/models/gpt2"); // Use GPT-2 or another free model
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + HUGGING_FACE_API_KEY);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String requestBody = "{\"inputs\": \"" + prompt + "\", \"parameters\": {\"max_length\": 100}}";

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        } else if (responseCode == 429) {
            System.err.println("⚠️ ERROR: Hugging Face API rate limit exceeded (429). Retrying...");
            throw new Exception("Rate limit exceeded");
        } else {
            System.err.println("⚠️ ERROR: Hugging Face API returned response code " + responseCode);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                System.err.println("Hugging Face API Error: " + br.readLine());
            }
        }
        return null;
    }

    private static void saveToDatabase(String title, String keywords) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO seo_keywords (title, keywords) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, title);
                statement.setString(2, keywords);
                statement.executeUpdate();
                System.out.println("✅ Saved to DB: " + title);
            }
        } catch (SQLException e) {
            System.err.println("❌ ERROR: Failed to save to database");
            e.printStackTrace();
        }
    }
}