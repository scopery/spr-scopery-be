package com.company.scopery.modules.aiagent.prompt.application;

import com.company.scopery.common.pagination.PageRequestUtils;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PromptVersionApplicationService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final PromptVersionRepository versionRepository;
    private final PromptTemplateRepository templateRepository;
    private final AiAgentActivityLogger activityLogger;

    public PromptVersionApplicationService(PromptVersionRepository versionRepository,
                                           PromptTemplateRepository templateRepository,
                                           AiAgentActivityLogger activityLogger) {
        this.versionRepository = versionRepository;
        this.templateRepository = templateRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public PromptVersionResponse createPromptVersion(CreatePromptVersionCommand command) {
        PromptTemplate template = findTemplateOrThrow(command.templateId());

        if (template.status() == PromptTemplateStatus.DEPRECATED) {
            throw AiAgentExceptions.promptVersionTemplateDeprecated(template.code().value());
        }

        PromptContentFormat contentFormat = AiAgentEnumParser.parseRequired(
                PromptContentFormat.class, command.contentFormat(),
                AiAgentErrorCatalog.INVALID_PROMPT_CONTENT_FORMAT.code(), "contentFormat");

        if (contentFormat == PromptContentFormat.JSON) {
            validateJsonContent(command.content());
        }

        int nextVersionNumber = versionRepository.getMaxVersionNumber(command.templateId()) + 1;

        PromptVersion version = PromptVersion.create(
                command.templateId(), nextVersionNumber, command.title(), command.content(),
                contentFormat, command.variableSchema(), command.changeNote());

        PromptVersion saved = versionRepository.save(version);

        activityLogger.logSuccess(AiAgentEntityTypes.PROMPT_VERSION, saved.id(),
                AiAgentActivityActions.CREATE_PROMPT_VERSION,
                "Prompt version created: " + template.code().value() + " v" + saved.versionNumber());

        return PromptVersionResponse.from(saved);
    }

    @Transactional
    public PromptVersionDetailResponse updatePromptVersion(UpdatePromptVersionCommand command) {
        PromptVersion version = findVersionOrThrow(command.id());

        if (version.status() != PromptVersionStatus.DRAFT) {
            throw AiAgentExceptions.promptVersionContentNotEditable(version.status().name());
        }

        PromptContentFormat contentFormat = AiAgentEnumParser.parseRequired(
                PromptContentFormat.class, command.contentFormat(),
                AiAgentErrorCatalog.INVALID_PROMPT_CONTENT_FORMAT.code(), "contentFormat");

        if (contentFormat == PromptContentFormat.JSON) {
            validateJsonContent(command.content());
        }

        version.update(command.title(), command.content(), contentFormat,
                command.variableSchema(), command.changeNote());

        PromptVersion saved = versionRepository.save(version);

        activityLogger.logSuccess(AiAgentEntityTypes.PROMPT_VERSION, saved.id(),
                AiAgentActivityActions.UPDATE_PROMPT_VERSION,
                "Prompt version updated: v" + saved.versionNumber());

        String templateCode = loadTemplateCode(saved.templateId());
        return PromptVersionDetailResponse.from(saved, templateCode);
    }

    @Transactional(readOnly = true)
    public PromptVersionDetailResponse getPromptVersionDetail(GetPromptVersionDetailQuery query) {
        PromptVersion version = findVersionOrThrow(query.id());
        String templateCode = loadTemplateCode(version.templateId());
        return PromptVersionDetailResponse.from(version, templateCode);
    }

    @Transactional(readOnly = true)
    public Page<PromptVersionResponse> searchPromptVersions(SearchPromptVersionQuery query) {
        PromptVersionStatus status = AiAgentEnumParser.parseOptional(
                PromptVersionStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_PROMPT_VERSION_STATUS.code(), "status");
        PromptContentFormat contentFormat = AiAgentEnumParser.parseOptional(
                PromptContentFormat.class, query.contentFormat(),
                AiAgentErrorCatalog.INVALID_PROMPT_CONTENT_FORMAT.code(), "contentFormat");

        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, AiAgentSortFields.CREATED_AT));

        return versionRepository
                .findAll(query.templateId(), status, contentFormat, pageable)
                .map(PromptVersionResponse::from);
    }

    @Transactional
    public PromptVersionDetailResponse activatePromptVersion(ActivatePromptVersionCommand command) {
        PromptVersion version = findVersionOrThrow(command.id());

        if (version.status() == PromptVersionStatus.ARCHIVED) {
            throw AiAgentExceptions.archivedPromptVersionCannotBeActivated(version.versionNumber());
        }

        PromptTemplate template = findTemplateOrThrow(version.templateId());

        if (template.status() != PromptTemplateStatus.ACTIVE) {
            throw AiAgentExceptions.promptVersionTemplateNotActive(template.code().value());
        }

        versionRepository.archiveOtherActiveVersions(version.templateId(), version.id());

        version.activate();
        PromptVersion saved = versionRepository.save(version);

        activityLogger.logSuccess(AiAgentEntityTypes.PROMPT_VERSION, saved.id(),
                AiAgentActivityActions.ACTIVATE_PROMPT_VERSION,
                "Prompt version activated: " + template.code().value() + " v" + saved.versionNumber());

        return PromptVersionDetailResponse.from(saved, template.code().value());
    }

    @Transactional
    public PromptVersionDetailResponse archivePromptVersion(ArchivePromptVersionCommand command) {
        PromptVersion version = findVersionOrThrow(command.id());
        version.archive();
        PromptVersion saved = versionRepository.save(version);

        activityLogger.logSuccess(AiAgentEntityTypes.PROMPT_VERSION, saved.id(),
                AiAgentActivityActions.ARCHIVE_PROMPT_VERSION,
                "Prompt version archived: v" + saved.versionNumber());

        String templateCode = loadTemplateCode(saved.templateId());
        return PromptVersionDetailResponse.from(saved, templateCode);
    }

    private PromptVersion findVersionOrThrow(UUID id) {
        return versionRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.promptVersionNotFound(id));
    }

    private PromptTemplate findTemplateOrThrow(UUID templateId) {
        return templateRepository.findById(templateId)
                .orElseThrow(() -> AiAgentExceptions.promptVersionTemplateNotFound(templateId));
    }

    private String loadTemplateCode(UUID templateId) {
        return templateRepository.findById(templateId)
                .map(t -> t.code().value())
                .orElse(null);
    }

    private void validateJsonContent(String content) {
        if (content == null || content.isBlank()) return;
        try {
            OBJECT_MAPPER.readTree(content);
        } catch (JsonProcessingException e) {
            throw AiAgentExceptions.invalidPromptContentJson();
        }
    }
}
