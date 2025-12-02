package com.legalsahyog.legalsahyoghub.controller;

import com.legalsahyog.legalsahyoghub.service.ChatGPTService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatGPTService chatGPTService;

    public ChatController(ChatGPTService chatGPTService) {
        this.chatGPTService = chatGPTService;
    }

    // ✅ This now matches: POST /api/chatbot/query
    @PostMapping("/query")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> req) {

        String userMessage = req.get("message");
        if (userMessage == null || userMessage.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("reply", "Please enter a question related to IPC or legal matters.");
            return ResponseEntity.badRequest().body(error);
        }

        // Get AI response
        String reply = chatGPTService.askChatGPT(userMessage);

        // Response JSON expected by React
        Map<String, String> response = new HashMap<>();
        response.put("reply", reply);

        return ResponseEntity.ok(response);
    }
}
