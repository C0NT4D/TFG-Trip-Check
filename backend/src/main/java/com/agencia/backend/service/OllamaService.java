package com.agencia.backend.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class OllamaService {

    private final RestTemplate restTemplate;
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";

    // 1. Inyecta RestTemplateBuilder en el constructor
    public OllamaService(RestTemplateBuilder restTemplateBuilder) {
        // 2. Configura timeouts más largos (p. ej. 60 segundos)
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(60).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(60).toMillis());
        
        this.restTemplate = restTemplateBuilder
                .requestFactory(() -> factory)
                .build();
    }

    public String ask(String prompt) {

        Map<String, Object> body = new HashMap<>();
        body.put("model", "tinyllama");
        body.put("prompt", prompt);
        body.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // 3. Usa el RestTemplate ya configurado
        ResponseEntity<Map> response = restTemplate.postForEntity(OLLAMA_URL, request, Map.class);

        // Comprobación de seguridad
        if (response.getBody() != null && response.getBody().get("response") != null) {
            return response.getBody().get("response").toString();
        } else {
            return "No se recibió una respuesta válida del asistente.";
        }
    }
}