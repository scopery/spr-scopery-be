package com.company.scopery.modules.aiassistant.workspaceconfig.application.action;

import com.company.scopery.modules.aiassistant.shared.activity.AiAssistantActivityLogger;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantActivityActions;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantEntityTypes;
import com.company.scopery.modules.aiassistant.workspaceconfig.application.command.UpsertAiAssistantWorkspaceConfigCommand;
import com.company.scopery.modules.aiassistant.workspaceconfig.application.response.AiAssistantWorkspaceConfigResponse;
import com.company.scopery.modules.aiassistant.workspaceconfig.domain.model.AiAssistantWorkspaceConfig;
import com.company.scopery.modules.aiassistant.workspaceconfig.domain.model.AiAssistantWorkspaceConfigRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpsertAiAssistantWorkspaceConfigAction {

    private final AiAssistantWorkspaceConfigRepository configRepository;
    private final AiAssistantActivityLogger activityLogger;

    public UpsertAiAssistantWorkspaceConfigAction(
            AiAssistantWorkspaceConfigRepository configRepository,
            AiAssistantActivityLogger activityLogger) {
        this.configRepository = configRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiAssistantWorkspaceConfigResponse execute(UpsertAiAssistantWorkspaceConfigCommand cmd) {
        AiAssistantWorkspaceConfig config = configRepository.findByWorkspaceId(cmd.workspaceId())
                .map(existing -> {
                    existing.update(
                            cmd.modelDeploymentId(),
                            cmd.modelProvider(),
                            cmd.modelName(),
                            cmd.systemPromptOverride(),
                            cmd.temperatureOverride(),
                            cmd.maxOutputTokensOverride()
                    );
                    return existing;
                })
                .orElseGet(() -> AiAssistantWorkspaceConfig.create(
                        cmd.workspaceId(),
                        cmd.modelDeploymentId(),
                        cmd.modelProvider(),
                        cmd.modelName(),
                        cmd.systemPromptOverride(),
                        cmd.temperatureOverride(),
                        cmd.maxOutputTokensOverride()
                ));

        AiAssistantWorkspaceConfig saved = configRepository.save(config);

        activityLogger.logSuccess(
                AiAssistantEntityTypes.AI_WORKSPACE_CONFIG,
                saved.id(),
                AiAssistantActivityActions.UPSERT_AI_WORKSPACE_CONFIG,
                "Workspace config upserted for workspaceId=" + saved.workspaceId()
        );

        return AiAssistantWorkspaceConfigResponse.from(saved);
    }
}
