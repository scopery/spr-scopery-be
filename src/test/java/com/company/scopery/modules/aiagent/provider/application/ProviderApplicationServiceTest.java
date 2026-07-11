package com.company.scopery.modules.aiagent.provider.application;
import com.company.scopery.modules.aiagent.provider.application.action.CreateProviderAction;
import com.company.scopery.modules.aiagent.provider.application.service.ProviderQueryService;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.aiagent.provider.application.command.CreateProviderCommand;
import com.company.scopery.modules.aiagent.provider.application.query.GetProviderDetailQuery;
import com.company.scopery.modules.aiagent.provider.application.response.ProviderResponse;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderStatus;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderType;
import com.company.scopery.modules.aiagent.provider.domain.model.Provider;
import com.company.scopery.modules.aiagent.provider.domain.model.ProviderRepository;
import com.company.scopery.modules.aiagent.provider.domain.valueobject.ProviderCode;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProviderActionTest {

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private AiAgentActivityLogger activityLogger;


    private CreateProviderAction createProviderAction;
    private ProviderQueryService providerQueryService;

    @BeforeEach
    void setUp() {
        createProviderAction = new CreateProviderAction(providerRepository, activityLogger);
        providerQueryService = new ProviderQueryService(providerRepository);
    }

    @Test
    void createProvider_success_returnsActiveProviderResponse() {
        CreateProviderCommand command = new CreateProviderCommand(
                "OpenAI", "openai", "LLM", "https://api.openai.com", "OpenAI");

        when(providerRepository.existsByCode(any())).thenReturn(false);
        when(providerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProviderResponse response = createProviderAction.execute(command);

        assertThat(response.name()).isEqualTo("OpenAI");
        assertThat(response.code()).isEqualTo("OPENAI");
        assertThat(response.status()).isEqualTo("ACTIVE");
        verify(providerRepository).save(any(Provider.class));
        verify(activityLogger).logSuccess(eq("PROVIDER"), any(UUID.class),
                eq("CREATE_PROVIDER"), any(String.class));
    }

    @Test
    void createProvider_duplicateCode_throwsAppExceptionWithConflict() {
        CreateProviderCommand command = new CreateProviderCommand(
                "OpenAI", "OPENAI", "LLM", "https://api.openai.com", null);

        when(providerRepository.existsByCode(any())).thenReturn(true);

        assertThatThrownBy(() -> createProviderAction.execute(command))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("OPENAI")
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(AiAgentErrorCatalog.PROVIDER_CODE_ALREADY_EXISTS.code());
                });

        verify(providerRepository, never()).save(any());
    }

    @Test
    void getProviderDetail_notFound_throwsAppExceptionWithNotFound() {
        UUID id = UUID.randomUUID();
        when(providerRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> providerQueryService.getProviderDetail(new GetProviderDetailQuery(id)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void createProvider_normalizesCodeToUppercase() {
        CreateProviderCommand command = new CreateProviderCommand(
                "Google", "google_gemini", "LLM", "https://generativelanguage.googleapis.com", null);

        when(providerRepository.existsByCode(any())).thenReturn(false);
        when(providerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProviderResponse response = createProviderAction.execute(command);

        assertThat(response.code()).isEqualTo("GOOGLE_GEMINI");
    }

    // --- helper ---

    @SuppressWarnings("unused")
    private Provider existingProvider(ProviderStatus status) {
        return Provider.reconstitute(
                UUID.randomUUID(), "OpenAI", ProviderCode.of("OPENAI"), ProviderType.LLM,
                "https://api.openai.com", null, status, Instant.now(), Instant.now());
    }
}