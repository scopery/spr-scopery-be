package com.company.scopery.modules.aiagent.prompt.application.action;

import com.company.scopery.modules.aiagent.agent.domain.enums.AgentStatus;
import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.prompt.application.command.ActivatePromptTemplateCommand;
import com.company.scopery.modules.aiagent.prompt.application.response.PromptTemplateDetailResponse;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptTemplateStatus;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplate;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplateRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ActivatePromptTemplateAction {

    private final PromptTemplateRepository templateRepository;
    private final AgentRepository agentRepository;
    private final AiAgentActivityLogger activityLogger;

    public ActivatePromptTemplateAction(PromptTemplateRepository templateRepository,
                                        AgentRepository agentRepository,
                                        AiAgentActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.agentRepository = agentRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public PromptTemplateDetailResponse execute(ActivatePromptTemplateCommand command) {
        PromptTemplate template = templateRepository.findById(command.id())
                .orElseThrow(() -> AiAgentExceptions.promptTemplateNotFound(command.id()));

        if (template.status() == PromptTemplateStatus.DEPRECATED) {
            throw AiAgentExceptions.deprecatedPromptTemplateCannotBeActivated(template.code().value());
        }

        Agent agent = agentRepository.findById(template.agentId())
                .orElseThrow(() -> AiAgentExceptions.promptTemplateAgentNotFound(template.agentId()));

        if (agent.status() != AgentStatus.ACTIVE) {
            throw AiAgentExceptions.promptTemplateAgentNotActive(agent.code().value());
        }

        template.activate();
        PromptTemplate saved = templateRepository.save(template);

        activityLogger.logSuccess(AiAgentEntityTypes.PROMPT_TEMPLATE, saved.id(),
                AiAgentActivityActions.ACTIVATE_PROMPT_TEMPLATE,
                "Prompt template activated: " + saved.code().value());

        return PromptTemplateDetailResponse.from(saved, agent.name());
    }
}
