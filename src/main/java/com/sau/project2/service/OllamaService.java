package com.sau.project2.service;

import com.sau.project2.dto.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class OllamaService {

    private final RestClient restClient;
    private final String model;

    public OllamaService(@Value("${ollama.url}") String url,
                         @Value("${ollama.model}") String model) {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofMinutes(5));   // CPU-only LLM can be slow
        this.restClient = RestClient.builder().baseUrl(url).requestFactory(factory).build();
        this.model = model;
    }

    public String chat(List<ChatMessage> messages) {
        Map<String, Object> request = Map.of(
                "model", model,
                "messages", messages,   // whole conversation -> model has context
                "stream", false);       // one complete JSON answer

        Map<?, ?> response = restClient.post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        Map<?, ?> message = (Map<?, ?>) response.get("message");
        return (String) message.get("content");
    }
}