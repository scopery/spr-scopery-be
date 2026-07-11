package com.company.scopery.modules.aiagent.prompt.application.action;

import com.company.scopery.modules.aiagent.prompt.application.command.ActivatePromptVersionCommand;
import com.company.scopery.modules.aiagent.prompt.application.response.PromptVersionDetailResponse;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptTemplateStatus;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptVersionStatus;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplate;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplateRepository;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersionRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ActivatePromptVersionAction {

    private final PromptVersionRepository versionRepository;
    private final PromptTemplateRepository templateRepository;
    private final AiAgentActivityLogger activityLogger;

    public ActivatePromptVersionAction(PromptVersionRepository versionRepository,
                                       PromptTemplateRepository templateRepository,
                                       AiAgentActivityLogger activityLogger) {
        this.versionRepository = versionRepository;
        this.templateRepository = templateRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public PromptVersionDetailResponse execute(ActivatePromptVersionCommand command) {
        PromptVersion version = versionRepository.findById(command.id())
                .orElseThrow(() -> AiAgentExceptions.promptVersionNotFound(command.id()));

        if (version.status() == PromptVersionStatus.ARCHIVED) {
            throw AiAgentExceptions.archivedPromptVersionCannotBeActivated(version.versionNumber());
        }

        PromptTemplate template = templateRepository.findById(version.templateId())
                .orElseThrow(() -> AiAgentExceptions.promptVersionTemplateNotFound(version.templateId()));

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
}
