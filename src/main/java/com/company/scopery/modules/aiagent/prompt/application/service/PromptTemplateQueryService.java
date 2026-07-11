package com.company.scopery.modules.aiagent.prompt.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.prompt.application.query.GetPromptTemplateDetailQuery;
import com.company.scopery.modules.aiagent.prompt.application.query.SearchPromptTemplateQuery;
import com.company.scopery.modules.aiagent.prompt.application.response.PromptTemplateDetailResponse;
import com.company.scopery.modules.aiagent.prompt.application.response.PromptTemplateResponse;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptTemplateStatus;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplate;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplateRepository;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentSortFields;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PromptTemplateQueryService {

    private final PromptTemplateRepository templateRepository;
    private final AgentRepository agentRepository;

    public PromptTemplateQueryService(PromptTemplateRepository templateRepository,
                                      AgentRepository agentRepository) {
        this.templateRepository = templateRepository;
        this.agentRepository = agentRepository;
    }

    @Transactional(readOnly = true)
    public PromptTemplateDetailResponse getPromptTemplateDetail(GetPromptTemplateDetailQuery query) {
        PromptTemplate template = templateRepository.findById(query.id())
                .orElseThrow(() -> AiAgentExceptions.promptTemplateNotFound(query.id()));
        String agentName = loadAgentName(template.agentId());
        return PromptTemplateDetailResponse.from(template, agentName);
    }

    @Transactional(readOnly = true)
    public PageResult<PromptTemplateResponse> searchPromptTemplates(SearchPromptTemplateQuery query) {
        PromptTemplateStatus status = AiAgentEnumParser.parseOptional(
                PromptTemplateStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_PROMPT_TEMPLATE_STATUS.code(), "status");

        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), AiAgentSortFields.CREATED_AT, false);

        return templateRepository
                .findAll(query.agentId(), query.keyword(), status, pageQuery)
                .map(PromptTemplateResponse::from);
    }

    private String loadAgentName(UUID agentId) {
        return agentRepository.findById(agentId).map(Agent::name).orElse(null);
    }
}
