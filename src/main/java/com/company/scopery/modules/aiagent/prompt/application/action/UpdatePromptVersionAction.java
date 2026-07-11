package com.company.scopery.modules.aiagent.prompt.application.action;

import com.company.scopery.modules.aiagent.prompt.application.command.UpdatePromptVersionCommand;
import com.company.scopery.modules.aiagent.prompt.application.response.PromptVersionDetailResponse;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptContentFormat;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptVersionStatus;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplateRepository;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersionRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UpdatePromptVersionAction {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final PromptVersionRepository versionRepository;
    private final PromptTemplateRepository templateRepository;
    private final AiAgentActivityLogger activityLogger;

    public UpdatePromptVersionAction(PromptVersionRepository versionRepository,
                                     PromptTemplateRepository templateRepository,
                                     AiAgentActivityLogger activityLogger) {
        this.versionRepository = versionRepository;
        this.templateRepository = templateRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public PromptVersionDetailResponse execute(UpdatePromptVersionCommand command) {
        PromptVersion version = versionRepository.findById(command.id())
                .orElseThrow(() -> AiAgentExceptions.promptVersionNotFound(command.id()));

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
