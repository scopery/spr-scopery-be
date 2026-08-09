package com.company.scopery.integration.ai.anthropic;

import com.company.scopery.integration.ai.AiProviderAdapter;
import com.company.scopery.integration.ai.AiProviderRequest;
import com.company.scopery.integration.ai.AiProviderResponse;
import com.company.scopery.modules.aiagent.providersecret.application.ProviderSecretResolver;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

@Component
public class AnthropicProviderAdapter implements AiProviderAdapter {

    static final String SUPPORTED_PROVIDER_CODE = "ANTHROPIC";

    private final AnthropicClient client;
    private final ProviderSecretResolver providerSecretResolver;
    private final String envApiKey;

    public AnthropicProviderAdapter(AnthropicClient client, ProviderSecretResolver providerSecretResolver,
                                    AnthropicProperties properties) {
        this.client = client;
        this.providerSecretResolver = providerSecretResolver;
        this.envApiKey = properties.apiKey();
    }

    @Override
    public String supportedProviderCode() {
        return SUPPORTED_PROVIDER_CODE;
    }

    @Override
    public AiProviderResponse call(AiProviderRequest request) {
        String apiKey = resolveApiKey(request);
        int maxTokens = request.maxOutputTokens() != null ? request.maxOutputTokens() : 4096;

        AnthropicRequest anthropicRequest = new AnthropicRequest(
                request.model(),
                request.systemPrompt(),
                List.of(Map.of("role", "user", "content", request.renderedPrompt())),
                maxTokens,
                request.temperature(),
                false
        );

        AnthropicResponse response;
        try {
            response = client.callMessages(apiKey, anthropicRequest);
        } catch (ResourceAccessException e) {
            throw AiAgentExceptions.anthropicApiTimeout();
        } catch (RestClientResponseException e) {
            throw AiAgentExceptions.anthropicApiCallFailed(e.getStatusCode().value());
        }

        if (response == null || response.id() == null) {
            throw AiAgentExceptions.anthropicApiResponseInvalid("response or id is null");
        }

        AnthropicResponse.Usage usage = response.usage();
        Integer inputTokens = usage != null ? usage.inputTokens() : null;
        Integer outputTokens = usage != null ? usage.outputTokens() : null;
        Integer totalTokens = (inputTokens != null && outputTokens != null) ? inputTokens + outputTokens : null;

        return new AiProviderResponse(
                response.id(),
                response.extractOutputText(),
                inputTokens,
                outputTokens,
                totalTokens,
                null
        );
    }

    private String resolveApiKey(AiProviderRequest request) {
        if (request.providerId() != null) {
            return providerSecretResolver.resolveApiKey(request.providerId());
        }
        if (envApiKey != null && !envApiKey.isBlank()) {
            return envApiKey;
        }
        return providerSecretResolver.resolveApiKeyByProviderCode(SUPPORTED_PROVIDER_CODE);
    }
}
