package com.company.scopery.integration.ai.openai;

import com.company.scopery.integration.ai.AiProviderAdapter;
import com.company.scopery.integration.ai.AiProviderRequest;
import com.company.scopery.integration.ai.AiProviderResponse;
import com.company.scopery.modules.aiagent.providersecret.application.ProviderSecretResolver;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class OpenAiProviderAdapter implements AiProviderAdapter {

    static final String SUPPORTED_PROVIDER_CODE = "OPENAI";

    private final OpenAiClient client;
    private final ProviderSecretResolver providerSecretResolver;
    private final String envApiKey;

    public OpenAiProviderAdapter(OpenAiClient client, ProviderSecretResolver providerSecretResolver,
                                  OpenAiProperties properties) {
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
        String apiKey;
        if (request.providerId() != null) {
            apiKey = providerSecretResolver.resolveApiKey(request.providerId());
        } else if (envApiKey != null && !envApiKey.isBlank()) {
            apiKey = envApiKey;
        } else {
            apiKey = providerSecretResolver.resolveApiKeyByProviderCode(SUPPORTED_PROVIDER_CODE);
        }

        OpenAiResponsesResponse response;
        try {
            OpenAiResponsesRequest openAiRequest = new OpenAiResponsesRequest(
                    request.model(), request.renderedPrompt());
            response = client.callResponses(apiKey, openAiRequest);
        } catch (ResourceAccessException e) {
            throw AiAgentExceptions.openAiApiTimeout();
        } catch (RestClientResponseException e) {
            throw AiAgentExceptions.openAiApiCallFailed(e.getStatusCode().value());
        }

        if (response == null || response.id() == null) {
            throw AiAgentExceptions.openAiApiResponseInvalid("response or id is null");
        }

        OpenAiResponsesResponse.Usage usage = response.usage();
        return new AiProviderResponse(
                response.id(),
                response.extractOutputText(),
                usage != null ? usage.inputTokens() : null,
                usage != null ? usage.outputTokens() : null,
                usage != null ? usage.totalTokens() : null,
                null
        );
    }
}
