package com.company.scopery.modules.aiagent.prompt.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.prompt.application.query.GetPromptVersionDetailQuery;
import com.company.scopery.modules.aiagent.prompt.application.query.SearchPromptVersionQuery;
import com.company.scopery.modules.aiagent.prompt.application.response.PromptVersionDetailResponse;
import com.company.scopery.modules.aiagent.prompt.application.response.PromptVersionResponse;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptContentFormat;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptVersionStatus;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplateRepository;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersionRepository;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentSortFields;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PromptVersionQueryService {

    private final PromptVersionRepository versionRepository;
    private final PromptTemplateRepository templateRepository;

    public PromptVersionQueryService(PromptVersionRepository versionRepository,
                                     PromptTemplateRepository templateRepository) {
        this.versionRepository = versionRepository;
        this.templateRepository = templateRepository;
    }

    @Transactional(readOnly = true)
    public PromptVersionDetailResponse getPromptVersionDetail(GetPromptVersionDetailQuery query) {
        PromptVersion version = versionRepository.findById(query.id())
                .orElseThrow(() -> AiAgentExceptions.promptVersionNotFound(query.id()));
        String templateCode = loadTemplateCode(version.templateId());
        return PromptVersionDetailResponse.from(version, templateCode);
    }

    @Transactional(readOnly = true)
    public PageResult<PromptVersionResponse> searchPromptVersions(SearchPromptVersionQuery query) {
        PromptVersionStatus status = AiAgentEnumParser.parseOptional(
                PromptVersionStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_PROMPT_VERSION_STATUS.code(), "status");
        PromptContentFormat contentFormat = AiAgentEnumParser.parseOptional(
                PromptContentFormat.class, query.contentFormat(),
                AiAgentErrorCatalog.INVALID_PROMPT_CONTENT_FORMAT.code(), "contentFormat");

        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), AiAgentSortFields.CREATED_AT, false);

        return versionRepository
                .findAll(query.templateId(), status, contentFormat, pageQuery)
                .map(PromptVersionResponse::from);
    }

    private String loadTemplateCode(UUID templateId) {
        return templateRepository.findById(templateId)
                .map(t -> t.code().value())
                .orElse(null);
    }
}
