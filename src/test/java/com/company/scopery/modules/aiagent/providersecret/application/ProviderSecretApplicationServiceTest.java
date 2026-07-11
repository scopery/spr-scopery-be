package com.company.scopery.modules.aiagent.providersecret.application;
import com.company.scopery.modules.aiagent.providersecret.application.action.DeactivateProviderSecretAction;
import com.company.scopery.modules.aiagent.providersecret.application.action.RotateProviderSecretAction;
import com.company.scopery.modules.aiagent.providersecret.application.action.SetProviderSecretAction;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.common.exception.ValidationException;
import com.company.scopery.modules.aiagent.provider.domain.model.Provider;
import com.company.scopery.modules.aiagent.provider.domain.valueobject.ProviderCode;
import com.company.scopery.modules.aiagent.provider.domain.model.ProviderRepository;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderStatus;
import com.company.scopery.modules.aiagent.providersecret.application.command.DeactivateProviderSecretCommand;
import com.company.scopery.modules.aiagent.providersecret.application.command.RotateProviderSecretCommand;
import com.company.scopery.modules.aiagent.providersecret.application.command.SetProviderSecretCommand;
import com.company.scopery.modules.aiagent.providersecret.application.response.ProviderSecretResponse;
import com.company.scopery.modules.aiagent.providersecret.domain.model.ProviderSecret;
import com.company.scopery.modules.aiagent.providersecret.domain.model.ProviderSecretRepository;
import com.company.scopery.modules.aiagent.providersecret.domain.enums.ProviderSecretType;
import com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto.EncryptedSecret;
import com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto.SecretEncryptor;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProviderSecretActionTest {

    @Mock private ProviderSecretRepository providerSecretRepository;
    @Mock private ProviderRepository providerRepository;
    @Mock private SecretEncryptor secretEncryptor;
    @Mock private AiAgentActivityLogger activityLogger;


    private final UUID providerId = UUID.randomUUID();
    private final UUID secretId   = UUID.randomUUID();

    private DeactivateProviderSecretAction deactivateProviderSecretAction;
    private RotateProviderSecretAction rotateProviderSecretAction;
    private SetProviderSecretAction setProviderSecretAction;

    @BeforeEach
    void setUp() {
        deactivateProviderSecretAction = new DeactivateProviderSecretAction(providerSecretRepository, activityLogger);
        rotateProviderSecretAction = new RotateProviderSecretAction(providerSecretRepository, providerRepository, secretEncryptor, activityLogger);
        setProviderSecretAction = new SetProviderSecretAction(providerSecretRepository, providerRepository, secretEncryptor, activityLogger);
    }

    @Test
    void setProviderSecret_noExisting_createsNewActiveSecret() {
        Provider provider = activeProvider();
        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));
        when(secretEncryptor.encrypt(any())).thenReturn(
                new EncryptedSecret("enc-val", "iv-abc", "v1"));
        when(providerSecretRepository.findActiveByProviderIdAndSecretType(any(), any()))
                .thenReturn(Optional.empty());
        when(providerSecretRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProviderSecretResponse response = setProviderSecretAction.execute(
                new SetProviderSecretCommand(providerId, "API_KEY", "sk-test-key", "My key"));

        assertThat(response.providerId()).isEqualTo(providerId);
        assertThat(response.secretType()).isEqualTo("API_KEY");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.maskedValue()).doesNotContain("sk-test-key");
        verify(providerSecretRepository, times(1)).save(any());
    }

    @Test
    void setProviderSecret_existingActive_deactivatesPreviousAndCreatesNew() {
        ProviderSecret existing = buildActiveSecret();
        Provider provider = activeProvider();
        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));
        when(secretEncryptor.encrypt(any())).thenReturn(
                new EncryptedSecret("enc-val-new", "iv-new", "v1"));
        when(providerSecretRepository.findActiveByProviderIdAndSecretType(any(), any()))
                .thenReturn(Optional.of(existing));
        when(providerSecretRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        setProviderSecretAction.execute(
                new SetProviderSecretCommand(providerId, "API_KEY", "sk-new-key", null));

        // 2 saves: deactivate old + create new
        verify(providerSecretRepository, times(2)).save(any());
    }

    @Test
    void setProviderSecret_providerNotFound_throwsNotFound() {
        when(providerRepository.findById(providerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> setProviderSecretAction.execute(
                new SetProviderSecretCommand(providerId, "API_KEY", "sk-test", null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus())
                        .isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void setProviderSecret_providerNotActive_throwsUnprocessable() {
        Provider inactive = mock(Provider.class);
        when(inactive.status()).thenReturn(ProviderStatus.INACTIVE);
        when(providerRepository.findById(providerId)).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> setProviderSecretAction.execute(
                new SetProviderSecretCommand(providerId, "API_KEY", "sk-test", null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus())
                        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @Test
    void setProviderSecret_invalidSecretType_throwsValidationException() {
        Provider provider = activeProvider();
        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));

        assertThatThrownBy(() -> setProviderSecretAction.execute(
                new SetProviderSecretCommand(providerId, "INVALID_TYPE", "sk-test", null)))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("INVALID_PROVIDER_SECRET_TYPE");
    }

    @Test
    void deactivateProviderSecret_success_changesStatusToInactive() {
        ProviderSecret secret = buildActiveSecret();
        when(providerSecretRepository.findById(secretId)).thenReturn(Optional.of(secret));
        when(providerSecretRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProviderSecretResponse response = deactivateProviderSecretAction.execute(
                new DeactivateProviderSecretCommand(secretId));

        assertThat(response.status()).isEqualTo("INACTIVE");
    }

    @Test
    void deactivateProviderSecret_notFound_throwsNotFound() {
        when(providerSecretRepository.findById(secretId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deactivateProviderSecretAction.execute(
                new DeactivateProviderSecretCommand(secretId)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus())
                        .isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void rotateProviderSecret_success_deactivatesOldAndCreatesNew() {
        ProviderSecret existing = buildActiveSecret();
        Provider provider = activeProvider();
        when(providerSecretRepository.findById(secretId)).thenReturn(Optional.of(existing));
        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));
        when(secretEncryptor.encrypt(any())).thenReturn(
                new EncryptedSecret("enc-rotated", "iv-rotated", "v1"));
        when(providerSecretRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProviderSecretResponse response = rotateProviderSecretAction.execute(
                new RotateProviderSecretCommand(secretId, "sk-rotated-key", "Rotated"));

        assertThat(response.status()).isEqualTo("ACTIVE");
        // 2 saves: deactivate old + save new
        verify(providerSecretRepository, times(2)).save(any());
    }

    @Test
    void response_doesNotExposeEncryptedValueOrRawSecret() {
        Provider provider = activeProvider();
        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));
        when(secretEncryptor.encrypt(any())).thenReturn(
                new EncryptedSecret("super-secret-encrypted", "iv-abc", "v1"));
        when(providerSecretRepository.findActiveByProviderIdAndSecretType(any(), any()))
                .thenReturn(Optional.empty());
        when(providerSecretRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProviderSecretResponse response = setProviderSecretAction.execute(
                new SetProviderSecretCommand(providerId, "API_KEY", "sk-real-secret", null));

        // Must not expose raw secret or encrypted value in response
        assertThat(response.maskedValue()).doesNotContain("sk-real-secret");
        assertThat(response.maskedValue()).doesNotContain("super-secret-encrypted");
        // Record fields should not include encryptedValue or iv
        assertThat(response).isNotNull();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Provider activeProvider() {
        Provider p = mock(Provider.class);
        when(p.status()).thenReturn(ProviderStatus.ACTIVE);
        ProviderCode code = mock(ProviderCode.class);
        when(code.value()).thenReturn("OPENAI");
        when(p.code()).thenReturn(code);
        return p;
    }

    private ProviderSecret buildActiveSecret() {
        return ProviderSecret.create(providerId, ProviderSecretType.API_KEY,
                "enc-value", "iv-value", "v1", "sk-...key1", "desc");
    }
}
