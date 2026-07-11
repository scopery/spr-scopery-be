package com.company.scopery.modules.aiagent.aimodel.application;
import com.company.scopery.modules.aiagent.aimodel.application.action.CreateAiModelAction;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.aiagent.aimodel.application.command.CreateAiModelCommand;
import com.company.scopery.modules.aiagent.aimodel.application.response.AiModelResponse;
import com.company.scopery.modules.aiagent.aimodel.domain.valueobject.AiModelCode;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.provider.domain.model.Provider;
import com.company.scopery.modules.aiagent.provider.domain.valueobject.ProviderCode;
import com.company.scopery.modules.aiagent.provider.domain.model.ProviderRepository;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderStatus;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderType;
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
class AiModelActionTest {

    @Mock
    private AiModelRepository aiModelRepository;

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private AiAgentActivityLogger activityLogger;


    private CreateAiModelAction createAiModelAction;

    @BeforeEach
    void setUp() {
        createAiModelAction = new CreateAiModelAction(aiModelRepository, providerRepository, activityLogger);
    }

    @Test
    void createAiModel_success_returnsActiveModelResponse() {
        UUID providerId = UUID.randomUUID();
        CreateAiModelCommand command = new CreateAiModelCommand(
                providerId, "GPT-4.1", "GPT_4_1", "gpt-4.1", "CHAT", null);

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(activeProvider(providerId)));
        when(aiModelRepository.existsByProviderIdAndCode(any(), any())).thenReturn(false);
        when(aiModelRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AiModelResponse response = createAiModelAction.execute(command);

        assertThat(response.name()).isEqualTo("GPT-4.1");
        assertThat(response.code()).isEqualTo("GPT_4_1");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.providerId()).isEqualTo(providerId);
        verify(aiModelRepository).save(any());
        verify(activityLogger).logSuccess(eq("AI_MODEL"), any(UUID.class),
                eq("CREATE_AI_MODEL"), any(String.class));
    }

    @Test
    void createAiModel_duplicateCode_throwsAppExceptionWithConflict() {
        UUID providerId = UUID.randomUUID();
        CreateAiModelCommand command = new CreateAiModelCommand(
                providerId, "GPT-4.1", "GPT_4_1", "gpt-4.1", "CHAT", null);

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(activeProvider(providerId)));
        when(aiModelRepository.existsByProviderIdAndCode(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> createAiModelAction.execute(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(AiAgentErrorCatalog.AI_MODEL_CODE_ALREADY_EXISTS.code());
                });

        verify(aiModelRepository, never()).save(any());
    }

    @Test
    void createAiModel_providerNotFound_throwsAppExceptionWithNotFound() {
        UUID providerId = UUID.randomUUID();
        CreateAiModelCommand command = new CreateAiModelCommand(
                providerId, "GPT-4.1", "GPT_4_1", "gpt-4.1", "CHAT", null);

        when(providerRepository.findById(providerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createAiModelAction.execute(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void createAiModel_inactiveProvider_throwsAppExceptionWithUnprocessable() {
        UUID providerId = UUID.randomUUID();
        CreateAiModelCommand command = new CreateAiModelCommand(
                providerId, "GPT-4.1", "GPT_4_1", "gpt-4.1", "CHAT", null);

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(inactiveProvider(providerId)));

        assertThatThrownBy(() -> createAiModelAction.execute(command))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("non-active")
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus())
                        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @Test
    void createAiModel_normalizesCodeToUppercase() {
        UUID providerId = UUID.randomUUID();
        CreateAiModelCommand command = new CreateAiModelCommand(
                providerId, "GPT-4.1", "gpt_4_1", "gpt-4.1", "CHAT", null);

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(activeProvider(providerId)));
        when(aiModelRepository.existsByProviderIdAndCode(any(), any())).thenReturn(false);
        when(aiModelRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AiModelResponse response = createAiModelAction.execute(command);

        assertThat(response.code()).isEqualTo("GPT_4_1");
    }

    // --- helpers ---

    private Provider activeProvider(UUID id) {
        return Provider.reconstitute(id, "OpenAI", ProviderCode.of("OPENAI"), ProviderType.LLM,
                "https://api.openai.com", null, ProviderStatus.ACTIVE, Instant.now(), Instant.now());
    }

    private Provider inactiveProvider(UUID id) {
        return Provider.reconstitute(id, "OpenAI", ProviderCode.of("OPENAI"), ProviderType.LLM,
                "https://api.openai.com", null, ProviderStatus.INACTIVE, Instant.now(), Instant.now());
    }
}