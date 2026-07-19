package com.company.scopery.modules.aiagent.provider.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.provider.application.command.DeactivateProviderCommand;
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
class DeactivateProviderActionTest {

    @Mock private ProviderRepository providerRepository;
    @Mock private ModelDeploymentRepository modelDeploymentRepository;
    @Mock private AiAgentActivityLogger activityLogger;

    private DeactivateProviderAction action;

    @BeforeEach
    void setUp() {
        action = new DeactivateProviderAction(providerRepository, modelDeploymentRepository, activityLogger);
    }

    @Test
    void deactivate_blockedWhenActiveDeploymentsExist() {
        UUID providerId = UUID.randomUUID();
        Provider provider = Provider.reconstitute(
                providerId, "OpenAI", ProviderCode.of("OPENAI"), ProviderType.LLM,
                "https://api.openai.com", null, ProviderStatus.ACTIVE, Instant.now(), Instant.now());
        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));
        when(modelDeploymentRepository.existsActiveByProviderId(providerId)).thenReturn(true);

        assertThatThrownBy(() -> action.execute(new DeactivateProviderCommand(providerId)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getErrorCode()).isEqualTo(AiAgentErrorCatalog.AI_PROVIDER_HAS_ACTIVE_DEPLOYMENTS.code());
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                });

        verify(providerRepository, never()).save(any());
    }

    @Test
    void deactivate_succeedsWhenNoActiveDeployments() {
        UUID providerId = UUID.randomUUID();
        Provider provider = Provider.reconstitute(
                providerId, "OpenAI", ProviderCode.of("OPENAI"), ProviderType.LLM,
                "https://api.openai.com", null, ProviderStatus.ACTIVE, Instant.now(), Instant.now());
        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));
        when(modelDeploymentRepository.existsActiveByProviderId(providerId)).thenReturn(false);
        when(providerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = action.execute(new DeactivateProviderCommand(providerId));

        assertThat(response.status()).isEqualTo("INACTIVE");
        verify(providerRepository).save(any(Provider.class));
    }
}
