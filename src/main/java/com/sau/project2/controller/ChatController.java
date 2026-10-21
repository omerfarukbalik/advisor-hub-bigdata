package com.sau.project2.controller;

import com.sau.project2.dto.ChatMessage;
import com.sau.project2.service.OllamaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final OllamaService ollamaService;

    @GetMapping("/chat")
    public String chatPage() {
        return "chat";
    }

    @PostMapping("/api/chat")
    @ResponseBody
    public Map<String, String> chat(@RequestBody List<ChatMessage> messages) {
        return Map.of("reply", ollamaService.chat(messages));
    }
}