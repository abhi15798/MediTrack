package com.airtribe.meditrack.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static com.airtribe.meditrack.constants.AppConstants.EQUALS;

public class AIHelper {

    private static AIHelper instance;

    // 1. Keep your API key secure (ideally read from an environment variable)
    private static final String API_KEY = System.getenv("API_KEY");
    private static final String API_URL = System.getenv("API_URL");;

    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2) // Force HTTP/2 for faster multiplexing
            .build();

    private AIHelper() {
    }

    public static synchronized AIHelper getInstance() {
        if (instance == null) {
            instance = new AIHelper();
        }
        return instance;
    }

    public String classifySpecialty(String humanDescription, List<String> availableSpecialties) {

        if (API_KEY == null || API_KEY.isEmpty()) {
            System.err.println("ERROR: The environment variable 'GEMINI_API_KEY' is not set!");
            return "General Physician"; // Fallback specialty
        }

        String specialtiesList = String.join(", ", availableSpecialties);
        String jsonPayload = getJsonPayload(humanDescription, specialtiesList);

        try {
            // Build the request, passing the API key in the custom Google header field
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", API_KEY) // Key handled here in the headers
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            System.out.println("AI is thinking...");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseGeminiContent(response.body());
            } else {
                System.out.println("Error Body: " + response.body());
            }
        } catch (Exception e) {
            System.err.println("Network exception: " + e.getMessage());
        }

        System.out.println("Fallback executed: ");
        return "General Physician";
    }

    private static String getJsonPayload(String humanDescription, String specialtiesList) {
        String completePrompt = "You are a clinic intake assistant. Analyze the user's symptoms and classify them into exactly ONE of these specialties: ["
                + specialtiesList + "]. Respond with only the name of the specialty. No extra words, no punctuation. "
                + "User symptoms: " + humanDescription.replace("\"", "\\\"");

        // Construct Google's mandatory nested JSON payload structure
        return "{"
                + "\"contents\": [{"
                + "    \"parts\": [{\"text\": \"" + completePrompt + "\"}]"
                + "}],"
                + "\"generationConfig\": {"
//                + "    \"maxOutputTokens\": 10,"  // Stops generation instantly once the word is found
                + "    \"temperature\": 0.0"
                + "}"
                + "}";
    }

    private String parseGeminiContent(String jsonResponse) {
        try {
            int textIndex = jsonResponse.indexOf("\"text\":");
            if (textIndex != -1) {
                int start = jsonResponse.indexOf("\"", textIndex + 7) + 1;
                int end = jsonResponse.indexOf("\"", start);
                String extracted = jsonResponse.substring(start, end).trim();
                System.out.println();
                System.out.println(EQUALS + " AI Suggested Doctor Specialization " + EQUALS);
                return extracted.replace("\\n", "").replace("\\\"", "\"").trim();
            }
        } catch (Exception e) {
            System.err.println("Failed to parse response: " + e.getMessage());
        }
        System.out.println("Fallback executed: ");
        return "General Physician";
    }

}
