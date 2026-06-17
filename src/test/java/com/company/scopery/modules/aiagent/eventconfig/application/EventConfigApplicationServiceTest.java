package com.company.scopery.modules.aiagent.eventconfig.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.aiagent.agent.domain.Agent;
import com.company.scopery.modules.aiagent.agent.domain.AgentCode;
import com.company.scopery.modules.aiagent.agent.domain.AgentRepository;
import com.company.scopery.modules.aiagent.agent.domain.AgentStatus;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentCode;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentEnvironment;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentStatus;
import com.company.scopery.modules.aiagent.eventconfig.application.command.*;
import com.company.scopery.modules.aiagent.eventconfig.application.query.*;
import com.company.scopery.modules.aiagent.eventconfig.application.response.*;
import com.company.scopery.modules.aiagent.eventconfig.domain.*;
import com.company.scopery.modules.aiagent.prompt.domain.*;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.*;
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
class EventConfigApplicationServiceTest {

    @Mock private EventConfigRepository eventConfigRepository;
    @Mock private EventDefinitionRepository eventDefinitionRepository;
    @Mock private AgentRepository agentRepository;
    @Mock private PromptVersionRepository promptVersionRepository;
    @Mock private PromptTemplateRepository promptTemplateRepository;
    @Mock private ModelDeploymentRepository modelDeploymentRepository;
    @Mock private AiAgentActivityLogger activityLogger;

    private EventConfigApplicationService service;

    private final UUID eventDefId  = UUID.randomUUID();
    private final UUID agentId     = UUID.randomUUID();
    private final UUID templateId  = UUID.randomUUID();
    private final UUID versionId   = UUID.randomUUID();
    private final UUID deploymentId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new EventConfigApplicationService(
                eventConfigRepository, eventDefinitionRepository, agentRepository,
                promptVersionRepository, promptTemplateRepository, modelDeploymentRepository,
                activityLogger, "DEV");
    }

    private CreateEventConfigCommand createCommand() {
        return new CreateEventConfigCommand(
                "HRM_CV_UPLOAD_DEV", "CV Upload DEV",
                eventDefId, "DEV", "EVENT",
                agentId, versionId, deploymentId, null, null);
    }

    private void mockAllDepsActive() {
        EventDefinition ed = EventDefinition.reconstitute(eventDefId,
                EventDefinitionCode.of("HRM_CV_UPLOADED"), "CV Uploaded",
                SourceSystemCode.of("HRM"), EventKey.of("CV_UPLOADED"),
                null, null, null, EventDefinitionStatus.ACTIVE,
                EventDefinition.INITIAL_VERSION, null, Instant.now(), Instant.now());
        when(eventDefinitionRepository.findById(eventDefId)).thenReturn(Optional.of(ed));

        Agent agent = Agent.reconstitute(agentId, "My Agent", AgentCode.of("MY_AGENT"),
                com.company.scopery.modules.aiagent.agent.domain.AgentType.GENERATION, null,
                null, null, AgentStatus.ACTIVE, Instant.now(), Instant.now());
        when(agentRepository.findById(agentId)).thenReturn(Optional.of(agent));

        PromptVersion version = PromptVersion.reconstitute(versionId, templateId, 1,
                "Title", "Content", PromptContentFormat.TEXT, null, null,
                PromptVersionStatus.ACTIVE, Instant.now(), Instant.now());
        when(promptVersionRepository.findById(versionId)).thenReturn(Optional.of(version));

        PromptTemplate template = PromptTemplate.reconstitute(templateId, agentId, "Template",
                PromptTemplateCode.of("TPL_CODE"), null, PromptTemplateStatus.ACTIVE,
                Instant.now(), Instant.now());
        when(promptTemplateRepository.findById(templateId)).thenReturn(Optional.of(template));

        ModelDeployment deployment = ModelDeployment.reconstitute(deploymentId, UUID.randomUUID(),
                "Deployment", ModelDeploymentCode.of("DEP_CODE"), ModelDeploymentEnvironment.DEV,
                "dep-id", null, null, null, false, null,
                ModelDeploymentStatus.ACTIVE, Instant.now(), Instant.now());
        when(modelDeploymentRepository.findById(deploymentId)).thenReturn(Optional.of(deployment));
    }

    // ── Create ────────────────────────────────────────────────────────────────

    @Test
    void create_success() {
        when(eventConfigRepository.existsByCode(any())).thenReturn(false);
        when(eventConfigRepository.existsActiveByEventDefinitionIdAndEnvironment(any(), any(), any())).thenReturn(false);
        mockAllDepsActive();
        when(eventConfigRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EventConfigResponse response = service.createEventConfig(createCommand());

        assertThat(response.code()).isEqualTo("HRM_CV_UPLOAD_DEV");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.environment()).isEqualTo("DEV");
    }

    @Test
    void create_duplicateCode_throwsConflict() {
        when(eventConfigRepository.existsByCode(any())).thenReturn(true);

        assertThatThrownBy(() -> service.createEventConfig(createCommand()))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            AiAgentErrorCatalog.EVENT_CONFIG_CODE_ALREADY_EXISTS.code());
                });

        verify(eventConfigRepository, never()).save(any());
    }

    @Test
    void create_activeAlreadyExists_throwsConflict() {
        when(eventConfigRepository.existsByCode(any())).thenReturn(false);
        when(eventConfigRepository.existsActiveByEventDefinitionIdAndEnvironment(any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.createEventConfig(createCommand()))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void create_inactiveEventDefinition_throwsUnprocessable() {
        when(eventConfigRepository.existsByCode(any())).thenReturn(false);
        when(eventConfigRepository.existsActiveByEventDefinitionIdAndEnvironment(any(), any(), any())).thenReturn(false);

        EventDefinition inactive = EventDefinition.reconstitute(eventDefId,
                EventDefinitionCode.of("HRM_CV"), "CV",
                SourceSystemCode.of("HRM"), EventKey.of("CV"),
                null, null, null, EventDefinitionStatus.INACTIVE,
                EventDefinition.INITIAL_VERSION, null, Instant.now(), Instant.now());
        when(eventDefinitionRepository.findById(eventDefId)).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> service.createEventConfig(createCommand()))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            AiAgentErrorCatalog.EVENT_CONFIG_EVENT_DEFINITION_NOT_ACTIVE.code());
                });
    }

    @Test
    void create_inactiveAgent_throwsUnprocessable() {
        when(eventConfigRepository.existsByCode(any())).thenReturn(false);
        when(eventConfigRepository.existsActiveByEventDefinitionIdAndEnvironment(any(), any(), any())).thenReturn(false);

        EventDefinition ed = EventDefinition.reconstitute(eventDefId,
                EventDefinitionCode.of("HRM_CV"), "CV", SourceSystemCode.of("HRM"),
                EventKey.of("CV"), null, null, null, EventDefinitionStatus.ACTIVE,
                EventDefinition.INITIAL_VERSION, null, Instant.now(), Instant.now());
        when(eventDefinitionRepository.findById(eventDefId)).thenReturn(Optional.of(ed));

        Agent inactive = Agent.reconstitute(agentId, "Agent", AgentCode.of("AGENT"),
                com.company.scopery.modules.aiagent.agent.domain.AgentType.GENERATION,
                null, null, null, AgentStatus.INACTIVE, Instant.now(), Instant.now());
        when(agentRepository.findById(agentId)).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> service.createEventConfig(createCommand()))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode()).isEqualTo(
                        AiAgentErrorCatalog.EVENT_CONFIG_AGENT_NOT_ACTIVE.code()));
    }

    @Test
    void create_inactivePromptVersion_throwsUnprocessable() {
        when(eventConfigRepository.existsByCode(any())).thenReturn(false);
        when(eventConfigRepository.existsActiveByEventDefinitionIdAndEnvironment(any(), any(), any())).thenReturn(false);

        EventDefinition ed = EventDefinition.reconstitute(eventDefId,
                EventDefinitionCode.of("HRM_CV"), "CV", SourceSystemCode.of("HRM"),
                EventKey.of("CV"), null, null, null, EventDefinitionStatus.ACTIVE,
                EventDefinition.INITIAL_VERSION, null, Instant.now(), Instant.now());
        when(eventDefinitionRepository.findById(eventDefId)).thenReturn(Optional.of(ed));

        Agent agent = Agent.reconstitute(agentId, "Agent", AgentCode.of("AGENT"),
                com.company.scopery.modules.aiagent.agent.domain.AgentType.GENERATION,
                null, null, null, AgentStatus.ACTIVE, Instant.now(), Instant.now());
        when(agentRepository.findById(agentId)).thenReturn(Optional.of(agent));

        PromptVersion draft = PromptVersion.reconstitute(versionId, templateId, 1,
                "Title", "Content", PromptContentFormat.TEXT, null, null,
                PromptVersionStatus.DRAFT, Instant.now(), Instant.now());
        when(promptVersionRepository.findById(versionId)).thenReturn(Optional.of(draft));

        assertThatThrownBy(() -> service.createEventConfig(createCommand()))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode()).isEqualTo(
                        AiAgentErrorCatalog.EVENT_CONFIG_PROMPT_VERSION_NOT_ACTIVE.code()));
    }

    @Test
    void create_promptTemplateAgentMismatch_throwsUnprocessable() {
        when(eventConfigRepository.existsByCode(any())).thenReturn(false);
        when(eventConfigRepository.existsActiveByEventDefinitionIdAndEnvironment(any(), any(), any())).thenReturn(false);

        EventDefinition ed = EventDefinition.reconstitute(eventDefId,
                EventDefinitionCode.of("HRM_CV"), "CV", SourceSystemCode.of("HRM"),
                EventKey.of("CV"), null, null, null, EventDefinitionStatus.ACTIVE,
                EventDefinition.INITIAL_VERSION, null, Instant.now(), Instant.now());
        when(eventDefinitionRepository.findById(eventDefId)).thenReturn(Optional.of(ed));

        Agent agent = Agent.reconstitute(agentId, "Agent", AgentCode.of("AGENT"),
                com.company.scopery.modules.aiagent.agent.domain.AgentType.GENERATION,
                null, null, null, AgentStatus.ACTIVE, Instant.now(), Instant.now());
        when(agentRepository.findById(agentId)).thenReturn(Optional.of(agent));

        PromptVersion version = PromptVersion.reconstitute(versionId, templateId, 1,
                "Title", "Content", PromptContentFormat.TEXT, null, null,
                PromptVersionStatus.ACTIVE, Instant.now(), Instant.now());
        when(promptVersionRepository.findById(versionId)).thenReturn(Optional.of(version));

        UUID differentAgentId = UUID.randomUUID();
        PromptTemplate template = PromptTemplate.reconstitute(templateId, differentAgentId,
                "Template", PromptTemplateCode.of("TPL_CODE"), null,
                PromptTemplateStatus.ACTIVE, Instant.now(), Instant.now());
        when(promptTemplateRepository.findById(templateId)).thenReturn(Optional.of(template));

        assertThatThrownBy(() -> service.createEventConfig(createCommand()))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode()).isEqualTo(
                        AiAgentErrorCatalog.EVENT_CONFIG_PROMPT_TEMPLATE_AGENT_MISMATCH.code()));
    }

    @Test
    void create_modelDeploymentEnvironmentMismatch_throwsUnprocessable() {
        when(eventConfigRepository.existsByCode(any())).thenReturn(false);
        when(eventConfigRepository.existsActiveByEventDefinitionIdAndEnvironment(any(), any(), any())).thenReturn(false);

        EventDefinition ed = EventDefinition.reconstitute(eventDefId,
                EventDefinitionCode.of("HRM_CV"), "CV", SourceSystemCode.of("HRM"),
                EventKey.of("CV"), null, null, null, EventDefinitionStatus.ACTIVE,
                EventDefinition.INITIAL_VERSION, null, Instant.now(), Instant.now());
        when(eventDefinitionRepository.findById(eventDefId)).thenReturn(Optional.of(ed));

        Agent agent = Agent.reconstitute(agentId, "Agent", AgentCode.of("AGENT"),
                com.company.scopery.modules.aiagent.agent.domain.AgentType.GENERATION,
                null, null, null, AgentStatus.ACTIVE, Instant.now(), Instant.now());
        when(agentRepository.findById(agentId)).thenReturn(Optional.of(agent));

        PromptVersion version = PromptVersion.reconstitute(versionId, templateId, 1,
                "Title", "Content", PromptContentFormat.TEXT, null, null,
                PromptVersionStatus.ACTIVE, Instant.now(), Instant.now());
        when(promptVersionRepository.findById(versionId)).thenReturn(Optional.of(version));

        PromptTemplate template = PromptTemplate.reconstitute(templateId, agentId, "Template",
                PromptTemplateCode.of("TPL_CODE"), null, PromptTemplateStatus.ACTIVE,
                Instant.now(), Instant.now());
        when(promptTemplateRepository.findById(templateId)).thenReturn(Optional.of(template));

        ModelDeployment prodDeployment = ModelDeployment.reconstitute(deploymentId, UUID.randomUUID(),
                "Prod Deployment", ModelDeploymentCode.of("DEP_PROD"), ModelDeploymentEnvironment.PROD,
                "dep-id", null, null, null, false, null,
                ModelDeploymentStatus.ACTIVE, Instant.now(), Instant.now());
        when(modelDeploymentRepository.findById(deploymentId)).thenReturn(Optional.of(prodDeployment));

        assertThatThrownBy(() -> service.createEventConfig(createCommand()))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode()).isEqualTo(
                        AiAgentErrorCatalog.EVENT_CONFIG_MODEL_DEPLOYMENT_ENVIRONMENT_MISMATCH.code()));
    }

    // ── Activate ──────────────────────────────────────────────────────────────

    @Test
    void activate_whenDeprecated_throwsUnprocessable() {
        UUID id = UUID.randomUUID();
        EventConfig deprecated = EventConfig.reconstitute(id,
                EventConfigCode.of("OLD_CONFIG"), "Old", UUID.randomUUID(),
                EventConfigEnvironment.DEV, EventTriggerType.EVENT,
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null, null,
                EventConfigStatus.DEPRECATED, Instant.now(), Instant.now());

        when(eventConfigRepository.findById(id)).thenReturn(Optional.of(deprecated));

        assertThatThrownBy(() -> service.activateEventConfig(new ActivateEventConfigCommand(id)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            AiAgentErrorCatalog.DEPRECATED_EVENT_CONFIG_CANNOT_BE_ACTIVATED.code());
                });
    }

    @Test
    void activate_anotherActiveAlreadyExists_throwsConflict() {
        UUID id = UUID.randomUUID();
        EventConfig inactive = EventConfig.reconstitute(id,
                EventConfigCode.of("CONFIG_INACTIVE"), "Inactive Config", eventDefId,
                EventConfigEnvironment.DEV, EventTriggerType.EVENT,
                agentId, versionId, deploymentId, null, null,
                EventConfigStatus.INACTIVE, Instant.now(), Instant.now());

        when(eventConfigRepository.findById(id)).thenReturn(Optional.of(inactive));
        when(eventConfigRepository.existsActiveByEventDefinitionIdAndEnvironment(
                eq(eventDefId), any(), eq(id))).thenReturn(true);

        assertThatThrownBy(() -> service.activateEventConfig(new ActivateEventConfigCommand(id)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    // ── Resolve ───────────────────────────────────────────────────────────────

    @Test
    void resolve_byEventDefinitionId_returnsActiveConfig() {
        UUID configId = UUID.randomUUID();
        EventConfig active = EventConfig.reconstitute(configId,
                EventConfigCode.of("HRM_CV_DEV"), "HRM CV DEV", eventDefId,
                EventConfigEnvironment.DEV, EventTriggerType.EVENT,
                agentId, versionId, deploymentId, null, null,
                EventConfigStatus.ACTIVE, Instant.now(), Instant.now());

        when(eventConfigRepository.findActiveByEventDefinitionIdAndEnvironment(
                eq(eventDefId), eq(EventConfigEnvironment.DEV))).thenReturn(Optional.of(active));
        when(eventDefinitionRepository.findById(eventDefId)).thenReturn(Optional.empty());
        when(agentRepository.findById(agentId)).thenReturn(Optional.empty());
        when(promptVersionRepository.findById(versionId)).thenReturn(Optional.empty());
        when(modelDeploymentRepository.findById(deploymentId)).thenReturn(Optional.empty());

        EventConfigDetailResponse response = service.resolveActiveEventConfig(
                new ResolveActiveEventConfigQuery(eventDefId, null, null, "DEV"));

        assertThat(response.code()).isEqualTo("HRM_CV_DEV");
        assertThat(response.status()).isEqualTo("ACTIVE");
    }

    @Test
    void resolve_notFound_throwsNotFound() {
        when(eventConfigRepository.findActiveByEventDefinitionIdAndEnvironment(any(), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.resolveActiveEventConfig(
                new ResolveActiveEventConfigQuery(UUID.randomUUID(), null, null, "DEV")))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    // ── Deactivate ────────────────────────────────────────────────────────────

    @Test
    void deactivate_setsInactive() {
        UUID id = UUID.randomUUID();
        EventConfig active = EventConfig.reconstitute(id,
                EventConfigCode.of("HRM_CV_DEV"), "HRM CV DEV", eventDefId,
                EventConfigEnvironment.DEV, EventTriggerType.EVENT,
                agentId, versionId, deploymentId, null, null,
                EventConfigStatus.ACTIVE, Instant.now(), Instant.now());

        when(eventConfigRepository.findById(id)).thenReturn(Optional.of(active));
        when(eventConfigRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(eventDefinitionRepository.findById(any())).thenReturn(Optional.empty());
        when(agentRepository.findById(any())).thenReturn(Optional.empty());
        when(promptVersionRepository.findById(any())).thenReturn(Optional.empty());
        when(modelDeploymentRepository.findById(any())).thenReturn(Optional.empty());

        EventConfigDetailResponse response = service.deactivateEventConfig(new DeactivateEventConfigCommand(id));
        assertThat(response.status()).isEqualTo("INACTIVE");
    }
}
