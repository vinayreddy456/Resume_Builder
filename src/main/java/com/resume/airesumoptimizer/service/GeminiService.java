package com.resume.airesumoptimizer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * MAIN METHOD → Optimize Resume using Gemini
     */
    public Map<String, Object> optimizeResume(String resumeText, String jobDescription) {
        try {

            String prompt = buildPrompt(resumeText, jobDescription);

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.4,
                            "topK", 40,
                            "topP", 0.9,
                            "maxOutputTokens", 2048
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String url = geminiApiUrl + "?key=" + geminiApiKey;

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, request, String.class);

            // 🔥 Extract Gemini text output
            String aiText = extractText(response.getBody());

            // 🔥 Convert AI JSON → Java Map
            return objectMapper.readValue(aiText, Map.class);

        } catch (Exception e) {
            throw new RuntimeException("Gemini optimization failed: " + e.getMessage(), e);
        }
    }

    /**
     * 🔥 STRONG PROMPT (IMPORTANT)
     */
    private String buildPrompt(String resumeText, String jobDescription) {
        return """
You are an expert ATS resume optimizer.

INPUT:
Resume:
%s

Job Description:
%s

TASK:
1. Analyze resume against job description
2. Improve resume using strong action verbs
3. Add relevant keywords for ATS
4. Keep content realistic (NO fake experience)

OUTPUT FORMAT (STRICT JSON ONLY):

{
  "ats_score": number,
  "matched_keywords": [],
  "missing_keywords": [],
  "improvements": [],
  "optimized_resume": "FULL formatted resume"
}

IMPORTANT RULES:
- Return ONLY valid JSON
- No explanations
- No extra text
""".formatted(resumeText, jobDescription);
    }

    /**
     * 🔥 Extract actual text from Gemini response
     */
    private String extractText(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);

            JsonNode candidates = root.path("candidates");

            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode parts = candidates.get(0)
                        .path("content")
                        .path("parts");

                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }

            throw new RuntimeException("Invalid Gemini response structure");

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini response: " + e.getMessage(), e);
        }
    }
}