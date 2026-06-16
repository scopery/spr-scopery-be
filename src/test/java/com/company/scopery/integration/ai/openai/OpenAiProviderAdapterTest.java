package com.company.scopery.integration.ai.openai;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.integration.ai.AiProviderRequest;
import com.company.scopery.integration.ai.AiProviderResponse;
import com.company.scopery.modules.aiagent.providersecret.application.ProviderSecretResolver;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OpenAiProviderAdapterTest {

    @Mock private OpenAiClient client;
    @Mock private ProviderSecretResolver providerSecretResolver;

    private OpenAiProviderAdapter adapter;

    private final UUID providerId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        adapter = new OpenAiProviderAdapter(client, providerSecretResolver);
        when(providerSecretResolver.resolveApiKey(providerId)).thenReturn("sk-test-key");
    }

    private AiProviderRequest request(String model) {
        return new AiProviderRequest(providerId, model, "Say hello", null, null);
    }

    @Test
    void supportedProviderCode_returnsOpenai() {
        assertThat(adapter.supportedProviderCode()).isEqualTo("OPENAI");
    }

    @Test
    void call_success_returnsResponse() {
        OpenAiResponsesResponse.Usage usage = new OpenAiResponsesResponse.Usage(100, 200, 300);
        OpenAiResponsesResponse.ContentItem content = new OpenAiResponsesResponse.ContentItem("output_text", "Hello!");
        OpenAiResponsesResponse.OutputItem output = new OpenAiResponsesResponse.OutputItem("message", List.of(content));
        OpenAiResponsesResponse apiResponse = new OpenAiResponsesResponse("resp-123", List.of(output), usage);

        when(client.callResponses(eq("sk-test-key"), any())).thenReturn(apiResponse);

        AiProviderResponse response = adapter.call(request("gpt-4o"));

        assertThat(response.providerRequestId()).isEqualTo("resp-123");
        assertThat(response.outputText()).isEqualTo("Hello!");
        assertThat(response.inputTokenCount()).isEqualTo(100);
        assertThat(response.outputTokenCount()).isEqualTo(200);
        assertThat(response.totalTokenCount()).isEqualTo(300);
        assertThat(response.estimatedCost()).isNull();
    }

    @Test
    void call_resolverThrows_propagatesException() {
        UUID unknownId = UUID.randomUUID();
        when(providerSecretResolver.resolveApiKey(unknownId))
                .thenThrow(AiAgentExceptions.providerSecretNotFound(unknownId));

        assertThatThrownBy(() -> adapter.call(
                new AiProviderRequest(unknownId, "gpt-4o", "prompt", null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ae.getErrorCode()).isEqualTo(AiAgentErrorCatalog.PROVIDER_SECRET_NOT_FOUND.code());
                });

        verifyNoInteractions(client);
    }

    @Test
    void call_timeout_throwsGatewayTimeout() {
        when(client.callResponses(any(), any())).thenThrow(new ResourceAccessException("Read timed out"));

        assertThatThrownBy(() -> adapter.call(request("gpt-4o")))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
                    assertThat(ae.getErrorCode()).isEqualTo(AiAgentErrorCatalog.OPENAI_API_TIMEOUT.code());
                });
    }

    @Test
    void call_apiError_throwsBadGateway() {
        RestClientResponseException apiError = mock(RestClientResponseException.class);
        when(apiError.getStatusCode()).thenReturn(HttpStatus.TOO_MANY_REQUESTS);
        when(apiError.getResponseBodyAsString()).thenReturn("{\"error\": \"rate limit\"}");
        when(client.callResponses(any(), any())).thenThrow(apiError);

        assertThatThrownBy(() -> adapter.call(request("gpt-4o")))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.BAD_GATEWAY);
                    assertThat(ae.getErrorCode()).isEqualTo(AiAgentErrorCatalog.OPENAI_API_CALL_FAILED.code());
                });
    }

    @Test
    void call_nullResponse_throwsBadGateway() {
        when(client.callResponses(any(), any())).thenReturn(null);

        assertThatThrownBy(() -> adapter.call(request("gpt-4o")))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.BAD_GATEWAY);
                    assertThat(ae.getErrorCode()).isEqualTo(AiAgentErrorCatalog.OPENAI_API_RESPONSE_INVALID.code());
                });
    }
}
