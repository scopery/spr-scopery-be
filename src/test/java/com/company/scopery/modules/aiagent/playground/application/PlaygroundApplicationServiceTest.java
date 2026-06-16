package com.company.scopery.modules.aiagent.playground.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.aiagent.agent.domain.AgentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.eventconfig.domain.EventConfig;
import com.company.scopery.modules.aiagent.eventconfig.domain.EventConfigRepository;
import com.company.scopery.modules.aiagent.eventconfig.domain.EventConfigStatus;
import com.company.scopery.modules.aiagent.execution.application.ExecutionApplicationService;
import com.company.scopery.modules.aiagent.execution.application.command.ExecuteEventConfigCommand;
import com.company.scopery.modules.aiagent.execution.application.command.ExecutePlaygroundDirectCommand;
import com.company.scopery.modules.aiagent.execution.application.prompt.PromptRenderPreviewResult;
import com.company.scopery.modules.aiagent.execution.application.prompt.PromptRenderer;
import com.company.scopery.modules.aiagent.execution.application.response.ExecutionRunResponse;
import com.company.scopery.modules.aiagent.execution.domain.ExecutionTriggerSource;
import com.company.scopery.modules.aiagent.playground.application.command.PreviewPromptCommand;
import com.company.scopery.modules.aiagent.playground.application.command.RunPlaygroundDirectCommand;
import com.company.scopery.modules.aiagent.playground.application.command.RunPlaygroundEventConfigCommand;
import com.company.scopery.modules.aiagent.playground.application.response.PlaygroundOptionResponse;
import com.company.scopery.modules.aiagent.playground.application.response.PlaygroundPromptPreviewResponse;
import com.company.scopery.modules.aiagent.playground.application.response.PlaygroundRunResponse;
import com.company.scopery.modules.aiagent.playground.mapper.PlaygroundResponseMapper;
import com.company.scopery.modules.aiagent.prompt.domain.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.domain.PromptVersionRepository;
import com.company.scopery.modules.aiagent.prompt.domain.PromptVersionStatus;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PlaygroundApplicationServiceTest {

    @Mock private ExecutionApplicationService executionApplicationService;
    @Mock private PromptRenderer promptRenderer;
    @Mock private EventConfigRepository eventConfigRepository;
    @Mock private AgentRepository agentRepository;
    @Mock private PromptVersionRepository promptVersionRepository;
    @Mock private ModelDeploymentRepository modelDeploymentRepository;
    @Mock private PlaygroundResponseMapper responseMapper;

    private PlaygroundApplicationService service;

    private final UUID eventConfigId   = UUID.randomUUID();
    private final UUID agentId         = UUID.randomUUID();
    private final UUID promptVersionId = UUID.randomUUID();
    private final UUID templateId      = UUID.randomUUID();
    private final UUID deploymentId    = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new PlaygroundApplicationService(
                executionApplicationService, promptRenderer,
                eventConfigRepository, agentRepository,
                promptVersionRepository, modelDeploymentRepository, responseMapper);
    }

    @Test
    void runEventConfig_delegatesToExecutionServiceWithPlaygroundTrigger() {
        EventConfig eventConfig = buildActiveEventConfig();
        when(eventConfigRepository.findById(eventConfigId)).thenReturn(Optional.of(eventConfig));

        ExecutionRunResponse execResponse = buildExecutionRunResponse();
        when(executionApplicationService.executeEventConfig(any())).thenReturn(execResponse);

        PlaygroundRunResponse mapped = buildPlaygroundRunResponse(PlaygroundMode.EVENT_CONFIG);
        when(responseMapper.toPlaygroundRunResponse(execResponse, PlaygroundMode.EVENT_CONFIG)).thenReturn(mapped);

        RunPlaygroundEventConfigCommand command = new RunPlaygroundEventConfigCommand(
                "req-1", eventConfigId, Map.of());

        PlaygroundRunResponse result = service.runEventConfig(command);

        assertThat(result).isSameAs(mapped);
        ArgumentCaptor<ExecuteEventConfigCommand> captor = ArgumentCaptor.forClass(ExecuteEventConfigCommand.class);
        verify(executionApplicationService).executeEventConfig(captor.capture());
        assertThat(captor.getValue().triggerSource()).isEqualTo(ExecutionTriggerSource.PLAYGROUND.name());
        assertThat(captor.getValue().eventConfigId()).isEqualTo(eventConfigId);
    }

    @Test
    void runEventConfig_notFound_throwsNotFound() {
        when(eventConfigRepository.findById(eventConfigId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.runEventConfig(
                new RunPlaygroundEventConfigCommand("req-1", eventConfigId, Map.of())))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ae.getErrorCode())
                            .isEqualTo(AiAgentErrorCatalog.PLAYGROUND_EVENT_CONFIG_NOT_FOUND.code());
                });
    }

    @Test
    void runEventConfig_notActive_throwsUnprocessable() {
        EventConfig inactive = mock(EventConfig.class);
        when(inactive.id()).thenReturn(eventConfigId);
        when(inactive.status()).thenReturn(EventConfigStatus.INACTIVE);
        when(eventConfigRepository.findById(eventConfigId)).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> service.runEventConfig(
                new RunPlaygroundEventConfigCommand("req-1", eventConfigId, Map.of())))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus())
                        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @Test
    void runDirect_delegatesToExecutionServiceDirect() {
        ExecutionRunResponse execResponse = buildExecutionRunResponse();
        when(executionApplicationService.executePlaygroundDirect(any())).thenReturn(execResponse);

        PlaygroundRunResponse mapped = buildPlaygroundRunResponse(PlaygroundMode.DIRECT);
        when(responseMapper.toPlaygroundRunResponse(execResponse, PlaygroundMode.DIRECT)).thenReturn(mapped);

        RunPlaygroundDirectCommand command = new RunPlaygroundDirectCommand(
                "req-2", agentId, promptVersionId, deploymentId, Map.of());

        PlaygroundRunResponse result = service.runDirect(command);

        assertThat(result).isSameAs(mapped);
        ArgumentCaptor<ExecutePlaygroundDirectCommand> captor =
                ArgumentCaptor.forClass(ExecutePlaygroundDirectCommand.class);
        verify(executionApplicationService).executePlaygroundDirect(captor.capture());
        assertThat(captor.getValue().agentId()).isEqualTo(agentId);
        assertThat(captor.getValue().promptVersionId()).isEqualTo(promptVersionId);
        assertThat(captor.getValue().modelDeploymentId()).isEqualTo(deploymentId);
    }

    @Test
    void previewPrompt_activeVersion_returnsRenderedPreview() {
        PromptVersion pv = buildActivePromptVersion();
        when(promptVersionRepository.findById(promptVersionId)).thenReturn(Optional.of(pv));
        when(promptRenderer.renderPreview("Hello, {{name}}!", Map.of("name", "World")))
                .thenReturn(new PromptRenderPreviewResult("Hello, World!", List.of()));

        PreviewPromptCommand command = new PreviewPromptCommand(promptVersionId, Map.of("name", "World"));
        PlaygroundPromptPreviewResponse result = service.previewPrompt(command);

        assertThat(result.renderedPrompt()).isEqualTo("Hello, World!");
        assertThat(result.missingVariables()).isEmpty();
        assertThat(result.promptVersionId()).isEqualTo(promptVersionId);
        assertThat(result.promptTemplateId()).isEqualTo(templateId);
        verify(executionApplicationService, never()).executeEventConfig(any());
        verify(executionApplicationService, never()).executePlaygroundDirect(any());
    }

    @Test
    void previewPrompt_draftVersion_allowed() {
        PromptVersion draftPv = mock(PromptVersion.class);
        when(draftPv.id()).thenReturn(promptVersionId);
        when(draftPv.templateId()).thenReturn(templateId);
        when(draftPv.status()).thenReturn(PromptVersionStatus.DRAFT);
        when(draftPv.content()).thenReturn("Draft prompt");
        when(promptVersionRepository.findById(promptVersionId)).thenReturn(Optional.of(draftPv));
        when(promptRenderer.renderPreview("Draft prompt", Map.of()))
                .thenReturn(new PromptRenderPreviewResult("Draft prompt", List.of()));

        PreviewPromptCommand command = new PreviewPromptCommand(promptVersionId, Map.of());
        PlaygroundPromptPreviewResponse result = service.previewPrompt(command);

        assertThat(result.renderedPrompt()).isEqualTo("Draft prompt");
    }

    @Test
    void previewPrompt_archivedVersion_throws() {
        PromptVersion archived = mock(PromptVersion.class);
        when(archived.id()).thenReturn(promptVersionId);
        when(archived.status()).thenReturn(PromptVersionStatus.ARCHIVED);
        when(promptVersionRepository.findById(promptVersionId)).thenReturn(Optional.of(archived));

        assertThatThrownBy(() -> service.previewPrompt(
                new PreviewPromptCommand(promptVersionId, Map.of())))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode())
                            .isEqualTo(AiAgentErrorCatalog.PLAYGROUND_PROMPT_VERSION_NOT_ACTIVE.code());
                });
    }

    @Test
    void previewPrompt_missingVariables_returnedInResponse() {
        PromptVersion pv = buildActivePromptVersion();
        when(promptVersionRepository.findById(promptVersionId)).thenReturn(Optional.of(pv));
        when(promptRenderer.renderPreview("Hello, {{name}}!", Map.of()))
                .thenReturn(new PromptRenderPreviewResult("Hello, {{name}}!", List.of("name")));

        PlaygroundPromptPreviewResponse result = service.previewPrompt(
                new PreviewPromptCommand(promptVersionId, Map.of()));

        assertThat(result.missingVariables()).containsExactly("name");
        assertThat(result.renderedPrompt()).isEqualTo("Hello, {{name}}!");
    }

    @Test
    void getOptions_allIncluded_returnsAllActiveLists() {
        when(eventConfigRepository.findAllByStatus(any())).thenReturn(List.of());
        when(agentRepository.findAllByStatus(any())).thenReturn(List.of());
        when(promptVersionRepository.findAllByStatus(any())).thenReturn(List.of());
        when(modelDeploymentRepository.findAllByStatus(any())).thenReturn(List.of());

        PlaygroundOptionResponse result = service.getOptions(true, true, true, true);

        assertThat(result.eventConfigs()).isNotNull();
        assertThat(result.agents()).isNotNull();
        assertThat(result.promptVersions()).isNotNull();
        assertThat(result.modelDeployments()).isNotNull();
    }

    @Test
    void getOptions_noneIncluded_allNulls() {
        PlaygroundOptionResponse result = service.getOptions(false, false, false, false);

        assertThat(result.eventConfigs()).isNull();
        assertThat(result.agents()).isNull();
        assertThat(result.promptVersions()).isNull();
        assertThat(result.modelDeployments()).isNull();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private EventConfig buildActiveEventConfig() {
        EventConfig ec = mock(EventConfig.class);
        when(ec.id()).thenReturn(eventConfigId);
        when(ec.status()).thenReturn(EventConfigStatus.ACTIVE);
        return ec;
    }

    private PromptVersion buildActivePromptVersion() {
        PromptVersion pv = mock(PromptVersion.class);
        when(pv.id()).thenReturn(promptVersionId);
        when(pv.templateId()).thenReturn(templateId);
        when(pv.status()).thenReturn(PromptVersionStatus.ACTIVE);
        when(pv.content()).thenReturn("Hello, {{name}}!");
        return pv;
    }

    private ExecutionRunResponse buildExecutionRunResponse() {
        return new ExecutionRunResponse(
                UUID.randomUUID(), "req-ok", "SUCCEEDED",
                eventConfigId, null, agentId, promptVersionId, deploymentId,
                "OPENAI", "GPT-4O", "gpt-4o-dep",
                "output", 10, 20, 30, new BigDecimal("0.01"),
                100L, "prov-req-1", null, null, "ALLOWED", List.of());
    }

    private PlaygroundRunResponse buildPlaygroundRunResponse(PlaygroundMode mode) {
        return new PlaygroundRunResponse(
                UUID.randomUUID(), "req-ok", "SUCCEEDED", mode.name(),
                null, null, agentId, promptVersionId, deploymentId,
                "OPENAI", "GPT-4O", "gpt-4o-dep",
                "output", 10, 20, 30, new BigDecimal("0.01"),
                100L, "prov-req-1", "ALLOWED", List.of(), null, null);
    }
}
