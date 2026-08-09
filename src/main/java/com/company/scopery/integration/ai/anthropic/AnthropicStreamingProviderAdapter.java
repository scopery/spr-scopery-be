package com.company.scopery.integration.ai.anthropic;

import com.company.scopery.integration.ai.AiChatMessage;
import com.company.scopery.integration.ai.AiStreamingProviderPort;
import com.company.scopery.integration.ai.AiStreamingRequest;
import com.company.scopery.integration.ai.StreamDeltaCallback;
import com.company.scopery.modules.aiagent.providersecret.application.ProviderSecretResolver;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AnthropicStreamingProviderAdapter implements AiStreamingProviderPort {

    private static final Logger log = LoggerFactory.getLogger(AnthropicStreamingProviderAdapter.class);
    private static final String SUPPORTED_PROVIDER_CODE = "ANTHROPIC";
    private static final String ANTHROPIC_VERSION = "2023-06-01";
    private static final String DATA_PREFIX = "data: ";

    private final RestClient restClient;
    private final ProviderSecretResolver providerSecretResolver;
    private final ObjectMapper objectMapper;
    private final String envApiKey;

    public AnthropicStreamingProviderAdapter(AnthropicProperties properties,
                                             ProviderSecretResolver providerSecretResolver,
                                             ObjectMapper objectMapper) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        int timeoutMs = properties.timeoutSeconds() * 1000;
        factory.setConnectTimeout(timeoutMs);
        factory.setReadTimeout(timeoutMs);

        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(factory)
                .build();
        this.providerSecretResolver = providerSecretResolver;
        this.objectMapper = objectMapper;
        this.envApiKey = properties.apiKey();
    }

    @Override
    public String supportedProviderCode() {
        return SUPPORTED_PROVIDER_CODE;
    }

    @Override
    public void streamChat(AiStreamingRequest request, StreamDeltaCallback callback) {
        String apiKey = resolveApiKey(request);
        Map<String, Object> body = buildRequestBody(request);

        try {
            restClient.post()
                    .uri("/messages")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", ANTHROPIC_VERSION)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .exchange((req, resp) -> {
                        if (resp.getStatusCode().isError()) {
                            String errorBody = new String(resp.getBody().readAllBytes(), StandardCharsets.UTF_8);
                            throw new org.springframework.web.client.HttpClientErrorException(
                                    resp.getStatusCode(),
                                    "Anthropic API error: " + errorBody,
                                    resp.getHeaders(),
                                    errorBody.getBytes(StandardCharsets.UTF_8),
                                    StandardCharsets.UTF_8);
                        }
                        try (InputStream is = resp.getBody();
                             BufferedReader reader = new BufferedReader(
                                     new InputStreamReader(is, StandardCharsets.UTF_8))) {
                            processStream(reader, callback);
                        }
                        return null;
                    });
        } catch (ResourceAccessException e) {
            throw AiAgentExceptions.anthropicApiTimeout();
        } catch (RestClientResponseException e) {
            throw AiAgentExceptions.anthropicApiCallFailed(e.getStatusCode().value());
        }
    }

    private void processStream(BufferedReader reader, StreamDeltaCallback callback) {
        Integer inputTokens = null;
        Integer outputTokens = null;
        String stopReason = null;
        boolean finalSent = false;

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(DATA_PREFIX)) continue;
                String data = line.substring(DATA_PREFIX.length()).trim();
                if (data.isBlank()) continue;

                JsonNode event;
                try {
                    event = objectMapper.readTree(data);
                } catch (Exception e) {
                    log.warn("[AnthropicStreamingAdapter] Failed to parse SSE data: {}", data);
                    continue;
                }

                String type = event.path("type").asText(null);
                if (type == null) continue;

                switch (type) {
                    case "message_start" -> {
                        JsonNode usage = event.path("message").path("usage");
                        if (!usage.isMissingNode()) {
                            inputTokens = usage.path("input_tokens").asInt(0);
                        }
                    }
                    case "content_block_delta" -> {
                        JsonNode delta = event.path("delta");
                        if ("text_delta".equals(delta.path("type").asText(null))) {
                            String text = delta.path("text").asText(null);
                            if (text != null && !text.isEmpty()) {
                                callback.onDelta(text, false, null, null, null);
                            }
                        }
                    }
                    case "message_delta" -> {
                        JsonNode delta = event.path("delta");
                        stopReason = delta.path("stop_reason").asText(null);
                        JsonNode usage = event.path("usage");
                        if (!usage.isMissingNode()) {
                            outputTokens = usage.path("output_tokens").asInt(0);
                        }
                    }
                    case "message_stop" -> {
                        callback.onDelta("", true, stopReason, inputTokens, outputTokens);
                        finalSent = true;
                    }
                }
            }

            if (!finalSent) {
                callback.onDelta("", true, stopReason, inputTokens, outputTokens);
            }
        } catch (Exception e) {
            log.warn("[AnthropicStreamingAdapter] Stream processing error", e);
            throw new RuntimeException("Stream processing failed: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> buildRequestBody(AiStreamingRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", request.model());
        body.put("stream", true);

        int maxTokens = request.maxOutputTokens() != null ? request.maxOutputTokens() : 4096;
        body.put("max_tokens", maxTokens);

        if (request.temperature() != null) {
            body.put("temperature", request.temperature());
        }

        List<Map<String, String>> messages = new ArrayList<>();
        for (AiChatMessage msg : request.messages()) {
            messages.add(Map.of("role", msg.role(), "content", msg.content()));
        }
        body.put("messages", messages);

        return body;
    }

    private String resolveApiKey(AiStreamingRequest request) {
        if (request.providerId() != null) {
            return providerSecretResolver.resolveApiKey(request.providerId());
        }
        if (envApiKey != null && !envApiKey.isBlank()) {
            return envApiKey;
        }
        return providerSecretResolver.resolveApiKeyByProviderCode(SUPPORTED_PROVIDER_CODE);
    }
}
