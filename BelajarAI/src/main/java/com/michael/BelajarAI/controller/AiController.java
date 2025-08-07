package com.michael.BelajarAI.controller;

import com.michael.BelajarAI.service.AiService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/ask")
    public Mono<String> ask(@RequestBody Map<String, String> request) {
        String q = request.get("message");
        return aiService.chat(q);
    }


}

