package com.company.scopery.modules.aiagent.deployment.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.aiagent.aimodel.domain.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.AiModelCode;
import com.company.scopery.modules.aiagent.aimodel.domain.AiModelRepository;
import com.company.scopery.modules.aiagent.aimodel.domain.AiModelStatus;
import com.company.scopery.modules.aiagent.aimodel.domain.AiModelType;
import com.company.scopery.modules.aiagent.deployment.application.command.CreateModelDeploymentCommand;
import com.company.scopery.modules.aiagent.deployment.application.command.SetDefaultModelDeploymentCommand;
import com.company.scopery.modules.aiagent.deployment.application.response.ModelDeploymentDetailResponse;
import com.company.scopery.modules.aiagent.deployment.application.response.ModelDeploymentResponse;
import com.company.scopery.modules.aiagent.deployment.domain.*;
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
class ModelDeploymentApplicationServiceTest {

    @Mock
    private ModelDeploymentRepository deploymentRepository;

    @Mock
    private AiModelRepository aiModelRepository;

    @Mock
    private AiAgentActivityLogger activityLogger;

    private ModelDeploymentApplicationService service;

    @BeforeEach
    void setUp() {
        service = new ModelDeploymentApplicationService(deploymentRepository, aiModelRepository, activityLogger);
    }

    @Test
    void createModelDeployment_success_returnsActiveResponse() {
        UUID modelId = UUID.randomUUID();
        CreateModelDeploymentCommand command = new CreateModelDeploymentCommand(
                modelId, "GPT-4.1 Prod", "GPT_4_1_PROD", "PROD", "gpt-4.1",
                null, null, null, false, null);

        when(aiModelRepository.findById(modelId)).thenReturn(Optional.of(activeModel(modelId)));
        when(deploymentRepository.existsByModelIdAndCode(any(), any())).thenReturn(false);
        when(deploymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ModelDeploymentResponse response = service.createModelDeployment(command);

        assertThat(response.name()).isEqualTo("GPT-4.1 Prod");
        assertThat(response.code()).isEqualTo("GPT_4_1_PROD");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.modelId()).isEqualTo(modelId);
        verify(deploymentRepository).save(any());
        verify(activityLogger).logSuccess(eq("MODEL_DEPLOYMENT"), any(UUID.class),
                eq("CREATE_MODEL_DEPLOYMENT"), any(String.class));
    }

    @Test
    void createModelDeployment_duplicateCode_throwsAppExceptionWithConflict() {
        UUID modelId = UUID.randomUUID();
        CreateModelDeploymentCommand command = new CreateModelDeploymentCommand(
                modelId, "GPT-4.1 Prod", "GPT_4_1_PROD", "PROD", "gpt-4.1",
                null, null, null, false, null);

        when(aiModelRepository.findById(modelId)).thenReturn(Optional.of(activeModel(modelId)));
        when(deploymentRepository.existsByModelIdAndCode(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.createModelDeployment(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(AiAgentErrorCatalog.MODEL_DEPLOYMENT_CODE_ALREADY_EXISTS.code());
                });

        verify(deploymentRepository, never()).save(any());
    }

    @Test
    void createModelDeployment_modelNotFound_throwsAppExceptionWithNotFound() {
        UUID modelId = UUID.randomUUID();
        CreateModelDeploymentCommand command = new CreateModelDeploymentCommand(
                modelId, "Test", "TEST", "PROD", "gpt-4.1",
                null, null, null, false, null);

        when(aiModelRepository.findById(modelId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createModelDeployment(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void createModelDeployment_inactiveModel_throwsAppExceptionWithUnprocessable() {
        UUID modelId = UUID.randomUUID();
        CreateModelDeploymentCommand command = new CreateModelDeploymentCommand(
                modelId, "Test", "TEST", "PROD", "gpt-4.1",
                null, null, null, false, null);

        when(aiModelRepository.findById(modelId)).thenReturn(Optional.of(inactiveModel(modelId)));

        assertThatThrownBy(() -> service.createModelDeployment(command))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("non-active")
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus())
                        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @Test
    void createModelDeployment_withIsDefault_clearsOtherDefaults() {
        UUID modelId = UUID.randomUUID();
        CreateModelDeploymentCommand command = new CreateModelDeploymentCommand(
                modelId, "GPT-4.1 Prod", "GPT_4_1_PROD", "PROD", "gpt-4.1",
                null, null, null, true, null);

        when(aiModelRepository.findById(modelId)).thenReturn(Optional.of(activeModel(modelId)));
        when(deploymentRepository.existsByModelIdAndCode(any(), any())).thenReturn(false);
        when(deploymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.createModelDeployment(command);

        verify(deploymentRepository).clearDefaultFlags(eq(modelId), eq(ModelDeploymentEnvironment.PROD), isNull());
    }

    @Test
    void setDefaultModelDeployment_clearsOtherDefaultsAndSets() {
        UUID modelId = UUID.randomUUID();
        UUID deploymentId = UUID.randomUUID();
        ModelDeployment deployment = activeDeployment(deploymentId, modelId);

        when(deploymentRepository.findById(deploymentId)).thenReturn(Optional.of(deployment));
        when(aiModelRepository.findById(modelId)).thenReturn(Optional.of(activeModel(modelId)));
        when(deploymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ModelDeploymentDetailResponse response = service.setDefaultModelDeployment(
                new SetDefaultModelDeploymentCommand(deploymentId));

        verify(deploymentRepository).clearDefaultFlags(eq(modelId), eq(ModelDeploymentEnvironment.PROD), eq(deploymentId));
        assertThat(response.isDefault()).isTrue();
    }

    // --- helpers ---

    private AiModel activeModel(UUID id) {
        return AiModel.reconstitute(id, UUID.randomUUID(), "GPT-4.1",
                AiModelCode.of("GPT_4_1"), "gpt-4.1", AiModelType.CHAT, null,
                AiModelStatus.ACTIVE, Instant.now(), Instant.now());
    }

    private AiModel inactiveModel(UUID id) {
        return AiModel.reconstitute(id, UUID.randomUUID(), "GPT-4.1",
                AiModelCode.of("GPT_4_1"), "gpt-4.1", AiModelType.CHAT, null,
                AiModelStatus.INACTIVE, Instant.now(), Instant.now());
    }

    private ModelDeployment activeDeployment(UUID id, UUID modelId) {
        return ModelDeployment.reconstitute(id, modelId, "GPT-4.1 Prod",
                ModelDeploymentCode.of("GPT_4_1_PROD"), ModelDeploymentEnvironment.PROD,
                "gpt-4.1", null, null, null, false, null,
                ModelDeploymentStatus.ACTIVE, Instant.now(), Instant.now());
    }
}