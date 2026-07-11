package com.company.scopery.modules.aiagent.prompt.application.action;

import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.prompt.application.command.DeactivatePromptTemplateCommand;
import com.company.scopery.modules.aiagent.prompt.application.response.PromptTemplateDetailResponse;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplate;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplateRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DeactivatePromptTemplateAction {

    private final PromptTemplateRepository templateRepository;
    private final AgentRepository agentRepository;
    private final AiAgentActivityLogger activityLogger;

    public DeactivatePromptTemplateAction(PromptTemplateRepository templateRepository,
                                          AgentRepository agentRepository,
                                          AiAgentActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.agentRepository = agentRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public PromptTemplateDetailResponse execute(DeactivatePromptTemplateCommand command) {
        PromptTemplate template = templateRepository.findById(command.id())
                .orElseThrow(() -> AiAgentExceptions.promptTemplateNotFound(command.id()));

        template.deactivate();
        PromptTemplate saved = templateRepository.save(template);

        activityLogger.logSuccess(AiAgentEntityTypes.PROMPT_TEMPLATE, saved.id(),
                AiAgentActivityActions.DEACTIVATE_PROMPT_TEMPLATE,
                "Prompt template deactivated: " + saved.code().value());

        String agentName = loadAgentName(saved.agentId());
        return PromptTemplateDetailResponse.from(saved, agentName);
    }

    private String loadAgentName(UUID agentId) {
        return agentRepository.findById(agentId).map(Agent::name).orElse(null);
    }
}
