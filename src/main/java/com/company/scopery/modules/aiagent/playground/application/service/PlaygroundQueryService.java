package com.company.scopery.modules.aiagent.playground.application.service;

import com.company.scopery.modules.aiagent.agent.domain.enums.AgentStatus;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentStatus;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventConfigStatus;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfigRepository;
import com.company.scopery.modules.aiagent.execution.application.prompt.PromptRenderer;
import com.company.scopery.modules.aiagent.playground.application.command.PreviewPromptCommand;
import com.company.scopery.modules.aiagent.playground.application.response.PlaygroundOptionResponse;
import com.company.scopery.modules.aiagent.playground.application.response.PlaygroundPromptPreviewResponse;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptVersionStatus;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersionRepository;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class PlaygroundQueryService {

    private final EventConfigRepository eventConfigRepository;
    private final AgentRepository agentRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final ModelDeploymentRepository modelDeploymentRepository;
    private final PromptRenderer promptRenderer;

    public PlaygroundQueryService(EventConfigRepository eventConfigRepository,
                                   AgentRepository agentRepository,
                                   PromptVersionRepository promptVersionRepository,
                                   ModelDeploymentRepository modelDeploymentRepository,
                                   PromptRenderer promptRenderer) {
        this.eventConfigRepository = eventConfigRepository;
        this.agentRepository = agentRepository;
        this.promptVersionRepository = promptVersionRepository;
        this.modelDeploymentRepository = modelDeploymentRepository;
        this.promptRenderer = promptRenderer;
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public PlaygroundPromptPreviewResponse previewPrompt(PreviewPromptCommand command) {
        var promptVersion = promptVersionRepository.findById(command.promptVersionId())
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
}
