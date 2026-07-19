package com.company.scopery.integration.ai.openai;

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
public class OpenAiStreamingProviderAdapter implements AiStreamingProviderPort {

    private static final Logger log = LoggerFactory.getLogger(OpenAiStreamingProviderAdapter.class);
    private static final String SUPPORTED_PROVIDER_CODE = "OPENAI";
    private static final String DATA_PREFIX = "data: ";
    private static final String DONE_MARKER = "[DONE]";

    private final RestClient restClient;
    private final ProviderSecretResolver providerSecretResolver;
    private final ObjectMapper objectMapper;

    public OpenAiStreamingProviderAdapter(OpenAiProperties properties,
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
    }

    @Override
    public String supportedProviderCode() {
        return SUPPORTED_PROVIDER_CODE;
    }

    @Override
    public void streamChat(AiStreamingRequest request, StreamDeltaCallback callback) {
        String apiKey = providerSecretResolver.resolveApiKey(request.providerId());

        Map<String, Object> body = buildRequestBody(request);

        try {
            restClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .exchange((req, resp) -> {
                        try (InputStream is = resp.getBody();
                             BufferedReader reader = new BufferedReader(
                                     new InputStreamReader(is, StandardCharsets.UTF_8))) {
                            processStream(reader, callback);
                        }
                        return null;
                    });
        } catch (ResourceAccessException e) {
            throw AiAgentExceptions.openAiApiTimeout();
        } catch (RestClientResponseException e) {
            throw AiAgentExceptions.openAiApiCallFailed(e.getStatusCode().value());
        }
    }

    private void processStream(BufferedReader reader, StreamDeltaCallback callback) {
        StringBuilder accumulatedText = new StringBuilder();
        Integer inputTokens = null;
        Integer outputTokens = null;

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(DATA_PREFIX)) continue;
                String data = line.substring(DATA_PREFIX.length()).trim();
                if (DONE_MARKER.equals(data)) break;

                JsonNode chunk = objectMapper.readTree(data);
                JsonNode choices = chunk.path("choices");
                JsonNode usageNode = chunk.path("usage");

                if (!usageNode.isMissingNode() && !usageNode.isNull()) {
                    inputTokens = usageNode.path("prompt_tokens").asInt(0);
                    outputTokens = usageNode.path("completion_tokens").asInt(0);
                }

                if (choices.isArray() && !choices.isEmpty()) {
                    JsonNode choice = choices.get(0);
                    JsonNode delta = choice.path("delta");
                    String finishReason = choice.path("finish_reason").asText(null);
                    boolean isDone = "stop".equals(finishReason) || "length".equals(finishReason)
                            || "content_filter".equals(finishReason);

                    String content = delta.path("content").asText(null);
                    if (content != null && !content.isEmpty()) {
                        accumulatedText.append(content);
                        callback.onDelta(content, isDone, finishReason, null, null);
                    } else if (isDone) {
                        callback.onDelta("", true, finishReason, inputTokens, outputTokens);
                    }
                }
            }

            // Final callback with token counts if not already sent
            if (inputTokens != null || outputTokens != null) {
                callback.onDelta("", true, "stop", inputTokens, outputTokens);
            }
        } catch (Exception e) {
            log.warn("[OpenAiStreamingAdapter] Stream processing error", e);
            throw new RuntimeException("Stream processing failed: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> buildRequestBody(AiStreamingRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", request.model());
        body.put("stream", true);
        body.put("stream_options", Map.of("include_usage", true));

        if (request.temperature() != null) {
            body.put("temperature", request.temperature());
        }
        if (request.maxOutputTokens() != null) {
            body.put("max_tokens", request.maxOutputTokens());
        }

        List<Map<String, String>> messages = new ArrayList<>();
        for (AiChatMessage msg : request.messages()) {
            messages.add(Map.of("role", msg.role(), "content", msg.content()));
        }
        body.put("messages", messages);

        return body;
    }
}
