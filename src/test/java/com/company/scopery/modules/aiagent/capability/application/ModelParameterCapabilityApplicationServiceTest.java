package com.company.scopery.modules.aiagent.capability.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.common.exception.ValidationException;
import com.company.scopery.modules.aiagent.aimodel.domain.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.AiModelCode;
import com.company.scopery.modules.aiagent.aimodel.domain.AiModelRepository;
import com.company.scopery.modules.aiagent.aimodel.domain.AiModelStatus;
import com.company.scopery.modules.aiagent.aimodel.domain.AiModelType;
import com.company.scopery.modules.aiagent.capability.application.command.CreateModelParameterCapabilityCommand;
import com.company.scopery.modules.aiagent.capability.application.response.ModelParameterCapabilityResponse;
import com.company.scopery.modules.aiagent.capability.domain.ModelParameterCapabilityRepository;
import com.company.scopery.modules.aiagent.capability.domain.ModelParameterName;
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
class ModelParameterCapabilityApplicationServiceTest {

    @Mock
    private ModelParameterCapabilityRepository capabilityRepository;

    @Mock
    private AiModelRepository aiModelRepository;

    @Mock
    private AiAgentActivityLogger activityLogger;

    private ModelParameterCapabilityApplicationService service;

    @BeforeEach
    void setUp() {
        service = new ModelParameterCapabilityApplicationService(
                capabilityRepository, aiModelRepository, activityLogger);
    }

    @Test
    void createModelParameterCapability_success_returnsActiveResponse() {
        UUID modelId = UUID.randomUUID();
        CreateModelParameterCapabilityCommand command = new CreateModelParameterCapabilityCommand(
                modelId, "temperature", "temperature", "YES", "NUMBER",
                null, null, null, false, null, null);

        when(aiModelRepository.findById(modelId)).thenReturn(Optional.of(activeModel(modelId)));
        when(capabilityRepository.existsByModelIdAndParameterName(any(), any())).thenReturn(false);
        when(capabilityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ModelParameterCapabilityResponse response = service.createModelParameterCapability(command);

        assertThat(response.parameterName()).isEqualTo("TEMPERATURE");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.modelId()).isEqualTo(modelId);
        verify(capabilityRepository).save(any());
        verify(activityLogger).logSuccess(eq("MODEL_PARAMETER_CAPABILITY"), any(UUID.class),
                eq("CREATE_MODEL_PARAMETER_CAPABILITY"), any(String.class));
    }

    @Test
    void createModelParameterCapability_normalizesParameterNameToUppercase() {
        UUID modelId = UUID.randomUUID();
        CreateModelParameterCapabilityCommand command = new CreateModelParameterCapabilityCommand(
                modelId, "max_output_tokens", "max_tokens", "YES", "INTEGER",
                null, null, null, false, null, null);

        when(aiModelRepository.findById(modelId)).thenReturn(Optional.of(activeModel(modelId)));
        when(capabilityRepository.existsByModelIdAndParameterName(any(), any())).thenReturn(false);
        when(capabilityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ModelParameterCapabilityResponse response = service.createModelParameterCapability(command);

        assertThat(response.parameterName()).isEqualTo("MAX_OUTPUT_TOKENS");
    }

    @Test
    void createModelParameterCapability_duplicateParameterName_throwsAppExceptionWithConflict() {
        UUID modelId = UUID.randomUUID();
        CreateModelParameterCapabilityCommand command = new CreateModelParameterCapabilityCommand(
                modelId, "TEMPERATURE", "temperature", "YES", "NUMBER",
                null, null, null, false, null, null);

        when(aiModelRepository.findById(modelId)).thenReturn(Optional.of(activeModel(modelId)));
        when(capabilityRepository.existsByModelIdAndParameterName(
                eq(modelId), eq(ModelParameterName.of("TEMPERATURE")))).thenReturn(true);

        assertThatThrownBy(() -> service.createModelParameterCapability(command))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("TEMPERATURE")
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            AiAgentErrorCatalog.MODEL_PARAMETER_CAPABILITY_ALREADY_EXISTS.code());
                });

        verify(capabilityRepository, never()).save(any());
    }

    @Test
    void createModelParameterCapability_modelNotFound_throwsAppExceptionWithNotFound() {
        UUID modelId = UUID.randomUUID();
        CreateModelParameterCapabilityCommand command = new CreateModelParameterCapabilityCommand(
                modelId, "TEMPERATURE", "temperature", "YES", "NUMBER",
                null, null, null, false, null, null);

        when(aiModelRepository.findById(modelId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createModelParameterCapability(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void createModelParameterCapability_deprecatedModel_throwsAppExceptionWithUnprocessable() {
        UUID modelId = UUID.randomUUID();
        CreateModelParameterCapabilityCommand command = new CreateModelParameterCapabilityCommand(
                modelId, "TEMPERATURE", "temperature", "YES", "NUMBER",
                null, null, null, false, null, null);

        when(aiModelRepository.findById(modelId)).thenReturn(Optional.of(deprecatedModel(modelId)));

        assertThatThrownBy(() -> service.createModelParameterCapability(command))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("deprecated")
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus())
                        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @Test
    void createModelParameterCapability_invalidIfNullBehavior_throwsValidationException() {
        UUID modelId = UUID.randomUUID();
        CreateModelParameterCapabilityCommand command = new CreateModelParameterCapabilityCommand(
                modelId, "TEMPERATURE", "temperature", "YES", "NUMBER",
                null, null, null, true, "INVALID_VALUE", null);

        when(aiModelRepository.findById(modelId)).thenReturn(Optional.of(activeModel(modelId)));
        when(capabilityRepository.existsByModelIdAndParameterName(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> service.createModelParameterCapability(command))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("INVALID_MODEL_PARAMETER_IF_NULL_BEHAVIOR");

        verify(capabilityRepository, never()).save(any());
    }

    // --- helpers ---

    private AiModel activeModel(UUID id) {
        return AiModel.reconstitute(id, UUID.randomUUID(), "GPT-4.1",
                AiModelCode.of("GPT_4_1"), "gpt-4.1", AiModelType.CHAT, null,
                AiModelStatus.ACTIVE, Instant.now(), Instant.now());
    }

    private AiModel deprecatedModel(UUID id) {
        return AiModel.reconstitute(id, UUID.randomUUID(), "GPT-3",
                AiModelCode.of("GPT_3"), "gpt-3", AiModelType.CHAT, null,
                AiModelStatus.DEPRECATED, Instant.now(), Instant.now());
    }
}
