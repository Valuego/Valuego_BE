package com.valuego.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class GeminiConfig {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Bean
    public RestTemplate template() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // Gemini 서버에 연결할 때 최대 5초
        factory.setConnectTimeout(Duration.ofSeconds(5));

        // Gemini 응답을 기다리는 최대 30초
        factory.setReadTimeout(Duration.ofSeconds(30));

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("x-goog-api-key", apiKey);
            request.getHeaders().set("Content-Type", "application/json");

            return execution.execute(request, body);
        });

        return restTemplate;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
