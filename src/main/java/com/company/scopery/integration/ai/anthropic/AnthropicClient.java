package com.company.scopery.integration.ai.anthropic;

import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AnthropicClient {

    private static final String ANTHROPIC_VERSION = "2023-06-01";

    private final RestClient restClient;

    public AnthropicClient(AnthropicProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        int timeoutMs = properties.timeoutSeconds() * 1000;
        factory.setConnectTimeout(timeoutMs);
        factory.setReadTimeout(timeoutMs);

        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(factory)
                .build();
    }

    public AnthropicResponse callMessages(String apiKey, AnthropicRequest request) {
        return restClient.post()
                .uri("/messages")
                .header("x-api-key", apiKey)
                .header("anthropic-version", ANTHROPIC_VERSION)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(AnthropicResponse.class);
    }
}
