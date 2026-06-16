package com.company.scopery.modules.aiagent.prompt.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.aiagent.agent.domain.Agent;
import com.company.scopery.modules.aiagent.agent.domain.AgentRepository;
import com.company.scopery.modules.aiagent.agent.domain.AgentStatus;
import com.company.scopery.modules.aiagent.prompt.application.command.*;
import com.company.scopery.modules.aiagent.prompt.application.query.*;
import com.company.scopery.modules.aiagent.prompt.application.response.*;
import com.company.scopery.modules.aiagent.prompt.domain.*;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentSortFields;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PromptTemplateApplicationService {

    private final PromptTemplateRepository templateRepository;
    private final AgentRepository agentRepository;
    private final AiAgentActivityLogger activityLogger;

    public PromptTemplateApplicationService(PromptTemplateRepository templateRepository,
                                            AgentRepository agentRepository,
                                            AiAgentActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.agentRepository = agentRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public PromptTemplateResponse createPromptTemplate(CreatePromptTemplateCommand command) {
        Agent agent = findAgentOrThrow(command.agentId());

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

    @Transactional
    public PromptTemplateDetailResponse updatePromptTemplate(UpdatePromptTemplateCommand command) {
        PromptTemplate template = findTemplateOrThrow(command.id());

        template.update(command.name(), command.description());
        PromptTemplate saved = templateRepository.save(template);

        activityLogger.logSuccess(AiAgentEntityTypes.PROMPT_TEMPLATE, saved.id(),
                AiAgentActivityActions.UPDATE_PROMPT_TEMPLATE,
                "Prompt template updated: " + saved.code().value());

        String agentName = loadAgentName(saved.agentId());
        return PromptTemplateDetailResponse.from(saved, agentName);
    }

    @Transactional(readOnly = true)
    public PromptTemplateDetailResponse getPromptTemplateDetail(GetPromptTemplateDetailQuery query) {
        PromptTemplate template = findTemplateOrThrow(query.id());
        String agentName = loadAgentName(template.agentId());
        return PromptTemplateDetailResponse.from(template, agentName);
    }

    @Transactional(readOnly = true)
    public Page<PromptTemplateResponse> searchPromptTemplates(SearchPromptTemplateQuery query) {
        PromptTemplateStatus status = AiAgentEnumParser.parseOptional(
                PromptTemplateStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_PROMPT_TEMPLATE_STATUS.code(), "status");

        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, AiAgentSortFields.CREATED_AT));

        return templateRepository
                .findAll(query.agentId(), query.keyword(), status, pageable)
                .map(PromptTemplateResponse::from);
    }

    @Transactional
    public PromptTemplateDetailResponse activatePromptTemplate(ActivatePromptTemplateCommand command) {
        PromptTemplate template = findTemplateOrThrow(command.id());

        if (template.status() == PromptTemplateStatus.DEPRECATED) {
            throw AiAgentExceptions.deprecatedPromptTemplateCannotBeActivated(template.code().value());
        }

        Agent agent = findAgentOrThrow(template.agentId());
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

    @Transactional
    public PromptTemplateDetailResponse deactivatePromptTemplate(DeactivatePromptTemplateCommand command) {
        PromptTemplate template = findTemplateOrThrow(command.id());
        template.deactivate();
        PromptTemplate saved = templateRepository.save(template);

        activityLogger.logSuccess(AiAgentEntityTypes.PROMPT_TEMPLATE, saved.id(),
                AiAgentActivityActions.DEACTIVATE_PROMPT_TEMPLATE,
                "Prompt template deactivated: " + saved.code().value());

        String agentName = loadAgentName(saved.agentId());
        return PromptTemplateDetailResponse.from(saved, agentName);
    }

    private PromptTemplate findTemplateOrThrow(UUID id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.promptTemplateNotFound(id));
    }

    private Agent findAgentOrThrow(UUID agentId) {
        return agentRepository.findById(agentId)
                .orElseThrow(() -> AiAgentExceptions.promptTemplateAgentNotFound(agentId));
    }

    private String loadAgentName(UUID agentId) {
        return agentRepository.findById(agentId).map(Agent::name).orElse(null);
    }
}