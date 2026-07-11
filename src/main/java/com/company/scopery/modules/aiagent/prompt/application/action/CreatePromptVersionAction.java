package com.company.scopery.modules.aiagent.prompt.application.action;

import com.company.scopery.modules.aiagent.prompt.application.command.CreatePromptVersionCommand;
import com.company.scopery.modules.aiagent.prompt.application.response.PromptVersionResponse;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptContentFormat;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptTemplateStatus;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplate;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreatePromptVersionAction {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final PromptVersionRepository versionRepository;
    private final PromptTemplateRepository templateRepository;
    private final AiAgentActivityLogger activityLogger;

    public CreatePromptVersionAction(PromptVersionRepository versionRepository,
                                     PromptTemplateRepository templateRepository,
                                     AiAgentActivityLogger activityLogger) {
        this.versionRepository = versionRepository;
        this.templateRepository = templateRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public PromptVersionResponse execute(CreatePromptVersionCommand command) {
        PromptTemplate template = templateRepository.findById(command.templateId())
                .orElseThrow(() -> AiAgentExceptions.promptVersionTemplateNotFound(command.templateId()));

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

        // getMaxVersionNumber() + save() is a read-then-write with no lock: two concurrent
        // creates on the same template can compute the same nextVersionNumber. The DB unique
        // constraint on (template_id, version_number) is the real safety net; translate its
        // violation into a clean 409 instead of letting it surface as a raw 500.
        PromptVersion saved;
        try {
            saved = versionRepository.save(version);
        } catch (DataIntegrityViolationException e) {
            throw AiAgentExceptions.promptVersionNumberConflict(command.templateId());
        }

        activityLogger.logSuccess(AiAgentEntityTypes.PROMPT_VERSION, saved.id(),
                AiAgentActivityActions.CREATE_PROMPT_VERSION,
                "Prompt version created: " + template.code().value() + " v" + saved.versionNumber());

        return PromptVersionResponse.from(saved);
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
