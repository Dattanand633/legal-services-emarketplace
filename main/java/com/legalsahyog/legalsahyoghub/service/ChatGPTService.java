package com.legalsahyog.legalsahyoghub.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatGPTService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String askChatGPT(String prompt) {

        String url = "https://openrouter.ai/api/v1/chat/completions";

        // Request body for OpenRouter
        Map<String, Object> body = new HashMap<>();
        body.put("model", "openai/gpt-3.5-turbo");  // or another supported model
        body.put("messages", List.of(
                Map.of(
                        "role", "user",
                        "content", prompt
                )
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("HTTP-Referer", "http://localhost");
        headers.set("X-Title", "LegalSahyogHub Assistant");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                return "Error: Empty response from AI API.";
            }

            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) responseBody.get("choices");

            if (choices == null || choices.isEmpty()) {
                return "Error: No choices returned from AI API.";
            }

            Map<String, Object> message =
                    (Map<String, Object>) choices.get(0).get("message");

            if (message == null) {
                return "Error: AI response had no message.";
            }

            Object content = message.get("content");
            return (content != null) ? content.toString() : "Error: AI response was empty.";

        } catch (Exception e) {
            return "Error calling AI API: " + e.getMessage();
        }
    }
}
