package com.michael.SpringAI.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public AiService(@Value("${spring.ai.openai.base-url}") String webUrl ,@Value("${openrouter.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(webUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.objectMapper = new ObjectMapper(); // inisialisasi ObjectMapper
    }

    public Mono<String> chat(String userMessage) {
        return webClient.post()
                .uri("/chat/completions")
                .bodyValue("""
                {
                   "model": "deepseek/deepseek-chat-v3-0324:free",
                   "messages":[
                       {"role":"system","content":"You are helpful."},
                       {"role":"user","content":"%s"}
                   ],
                   "temperature": 0.7,
                   "max_tokens": 500
                }
                """.formatted(userMessage))
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(response);
                        return jsonNode
                                .path("choices")
                                .get(0)
                                .path("message")
                                .path("content")
                                .asText();
                    } catch (Exception e) {
                        return "Gagal parsing respons AI.";
                    }
                });
    }
}
