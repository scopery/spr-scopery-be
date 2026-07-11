package com.company.scopery.modules.aiagent.prompt.application.action;

import com.company.scopery.modules.aiagent.agent.domain.enums.AgentStatus;
import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.prompt.application.command.CreatePromptTemplateCommand;
import com.company.scopery.modules.aiagent.prompt.application.response.PromptTemplateResponse;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplate;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplateRepository;
import com.company.scopery.modules.aiagent.prompt.domain.valueobject.PromptTemplateCode;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreatePromptTemplateAction {

    private final PromptTemplateRepository templateRepository;
    private final AgentRepository agentRepository;
    private final AiAgentActivityLogger activityLogger;

    public CreatePromptTemplateAction(PromptTemplateRepository templateRepository,
                                      AgentRepository agentRepository,
                                      AiAgentActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.agentRepository = agentRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public PromptTemplateResponse execute(CreatePromptTemplateCommand command) {
        Agent agent = agentRepository.findById(command.agentId())
                .orElseThrow(() -> AiAgentExceptions.promptTemplateAgentNotFound(command.agentId()));

        if (agent.status() == AgentStatus.DEPRECATED) {
            throw AiAgentExceptions.promptTemplateAgentDeprecated(agent.code().value());
        }

        PromptTemplateCode code = PromptTemplateCode.of(command.code());

        if (templateRepository.existsByAgentIdAndCode(command.agentId(), code)) {
            throw AiAgentExceptions.promptTemplateCodeAlreadyExists(code.value());
        }

        PromptTemplate template = PromptTemplate.create(
                command.agentId(), command.name(), code, command.description());

        PromptTemplate saved = templateRepository.save(template);

        activityLogger.logSuccess(AiAgentEntityTypes.PROMPT_TEMPLATE, saved.id(),
                AiAgentActivityActions.CREATE_PROMPT_TEMPLATE,
                "Prompt template created: " + saved.code().value());

        return PromptTemplateResponse.from(saved);
    }
}
