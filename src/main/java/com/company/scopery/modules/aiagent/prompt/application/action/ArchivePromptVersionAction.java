package com.company.scopery.modules.aiagent.prompt.application.action;

import com.company.scopery.modules.aiagent.prompt.application.command.ArchivePromptVersionCommand;
import com.company.scopery.modules.aiagent.prompt.application.response.PromptVersionDetailResponse;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptVersionStatus;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplateRepository;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersionRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArchivePromptVersionAction {

    private final PromptVersionRepository versionRepository;
    private final PromptTemplateRepository templateRepository;
    private final AiAgentActivityLogger activityLogger;

    public ArchivePromptVersionAction(PromptVersionRepository versionRepository,
                                      PromptTemplateRepository templateRepository,
                                      AiAgentActivityLogger activityLogger) {
        this.versionRepository = versionRepository;
        this.templateRepository = templateRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public PromptVersionDetailResponse execute(ArchivePromptVersionCommand command) {
        PromptVersion version = versionRepository.findById(command.id())
                .orElseThrow(() -> AiAgentExceptions.promptVersionNotFound(command.id()));

        if (version.status() == PromptVersionStatus.ARCHIVED) {
            throw AiAgentExceptions.promptVersionAlreadyArchived(version.versionNumber());
        }

        version.archive();
        PromptVersion saved = versionRepository.save(version);

        activityLogger.logSuccess(AiAgentEntityTypes.PROMPT_VERSION, saved.id(),
                AiAgentActivityActions.ARCHIVE_PROMPT_VERSION,
                "Prompt version archived: v" + saved.versionNumber());

        String templateCode = loadTemplateCode(saved.templateId());
        return PromptVersionDetailResponse.from(saved, templateCode);
    }

    private String loadTemplateCode(UUID templateId) {
        return templateRepository.findById(templateId)
                .map(t -> t.code().value())
                .orElse(null);
    }
}
