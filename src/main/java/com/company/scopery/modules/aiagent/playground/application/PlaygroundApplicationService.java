package com.company.scopery.modules.aiagent.playground.application;

import com.company.scopery.modules.aiagent.agent.domain.AgentRepository;
import com.company.scopery.modules.aiagent.agent.domain.AgentStatus;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentStatus;
import com.company.scopery.modules.aiagent.eventconfig.domain.EventConfig;
import com.company.scopery.modules.aiagent.eventconfig.domain.EventConfigRepository;
import com.company.scopery.modules.aiagent.eventconfig.domain.EventConfigStatus;
import com.company.scopery.modules.aiagent.execution.application.ExecutionApplicationService;
import com.company.scopery.modules.aiagent.execution.application.command.ExecuteEventConfigCommand;
import com.company.scopery.modules.aiagent.execution.application.command.ExecutePlaygroundDirectCommand;
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
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class PlaygroundApplicationService {

    private final ExecutionApplicationService executionApplicationService;
    private final PromptRenderer promptRenderer;
    private final EventConfigRepository eventConfigRepository;
    private final AgentRepository agentRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final ModelDeploymentRepository modelDeploymentRepository;
    private final PlaygroundResponseMapper responseMapper;

    public PlaygroundApplicationService(ExecutionApplicationService executionApplicationService,
                                         PromptRenderer promptRenderer,
                                         EventConfigRepository eventConfigRepository,
                                         AgentRepository agentRepository,
                                         PromptVersionRepository promptVersionRepository,
                                         ModelDeploymentRepository modelDeploymentRepository,
                                         PlaygroundResponseMapper responseMapper) {
        this.executionApplicationService = executionApplicationService;
        this.promptRenderer = promptRenderer;
        this.eventConfigRepository = eventConfigRepository;
        this.agentRepository = agentRepository;
        this.promptVersionRepository = promptVersionRepository;
        this.modelDeploymentRepository = modelDeploymentRepository;
        this.responseMapper = responseMapper;
    }

    public PlaygroundRunResponse runEventConfig(RunPlaygroundEventConfigCommand command) {
        EventConfig eventConfig = eventConfigRepository.findById(command.eventConfigId())
                .orElseThrow(() -> AiAgentExceptions.playgroundEventConfigNotFound(command.eventConfigId()));
        if (eventConfig.status() != EventConfigStatus.ACTIVE) {
            throw AiAgentExceptions.playgroundEventConfigNotActive(command.eventConfigId());
        }

        ExecuteEventConfigCommand execCommand = new ExecuteEventConfigCommand(
                command.requestId(),
                command.eventConfigId(),
                ExecutionTriggerSource.PLAYGROUND.name(),
                command.inputVariables());

        ExecutionRunResponse result = executionApplicationService.executeEventConfig(execCommand);
        return responseMapper.toPlaygroundRunResponse(result, PlaygroundMode.EVENT_CONFIG);
    }

    public PlaygroundRunResponse runDirect(RunPlaygroundDirectCommand command) {
        ExecutePlaygroundDirectCommand execCommand = new ExecutePlaygroundDirectCommand(
                command.requestId(),
                command.agentId(),
                command.promptVersionId(),
                command.modelDeploymentId(),
                command.inputVariables());

        ExecutionRunResponse result = executionApplicationService.executePlaygroundDirect(execCommand);
        return responseMapper.toPlaygroundRunResponse(result, PlaygroundMode.DIRECT);
    }

    public PlaygroundPromptPreviewResponse previewPrompt(PreviewPromptCommand command) {
        PromptVersion promptVersion = promptVersionRepository.findById(command.promptVersionId())
                .orElseThrow(() -> AiAgentExceptions.playgroundPromptVersionNotFound(command.promptVersionId()));

        if (promptVersion.status() != PromptVersionStatus.ACTIVE
                && promptVersion.status() != PromptVersionStatus.DRAFT) {
            throw AiAgentExceptions.playgroundPromptVersionNotActive(promptVersion.id());
        }

        var preview = promptRenderer.renderPreview(promptVersion.content(), command.inputVariables());

        return new PlaygroundPromptPreviewResponse(
                promptVersion.id(),
                promptVersion.templateId(),
                preview.renderedText(),
                preview.missingVariables(),
                Instant.now());
    }

    public PlaygroundOptionResponse getOptions(boolean includeEventConfigs, boolean includeAgents,
                                                boolean includePromptVersions, boolean includeModelDeployments) {
        List<PlaygroundOptionResponse.EventConfigItem> eventConfigs = null;
        if (includeEventConfigs) {
            eventConfigs = eventConfigRepository.findAllByStatus(EventConfigStatus.ACTIVE).stream()
                    .map(ec -> new PlaygroundOptionResponse.EventConfigItem(
                            ec.id(), ec.code().value(), ec.name(),
                            ec.status().name(), ec.environment().name()))
                    .toList();
        }

        List<PlaygroundOptionResponse.AgentItem> agents = null;
        if (includeAgents) {
            agents = agentRepository.findAllByStatus(AgentStatus.ACTIVE).stream()
                    .map(a -> new PlaygroundOptionResponse.AgentItem(
                            a.id(), a.code().value(), a.name(), a.status().name()))
                    .toList();
        }

        List<PlaygroundOptionResponse.PromptVersionItem> promptVersions = null;
        if (includePromptVersions) {
            promptVersions = promptVersionRepository.findAllByStatus(PromptVersionStatus.ACTIVE).stream()
                    .map(pv -> new PlaygroundOptionResponse.PromptVersionItem(
                            pv.id(), pv.templateId(), pv.versionNumber(), pv.status().name()))
                    .toList();
        }

        List<PlaygroundOptionResponse.ModelDeploymentItem> modelDeployments = null;
        if (includeModelDeployments) {
            modelDeployments = modelDeploymentRepository.findAllByStatus(ModelDeploymentStatus.ACTIVE).stream()
                    .map(d -> new PlaygroundOptionResponse.ModelDeploymentItem(
                            d.id(), d.code().value(), d.name(),
                            d.status().name(), d.environment().name()))
                    .toList();
        }

        return new PlaygroundOptionResponse(eventConfigs, agents, promptVersions, modelDeployments);
    }
}
