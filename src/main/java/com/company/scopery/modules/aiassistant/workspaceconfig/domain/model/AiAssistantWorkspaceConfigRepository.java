package com.company.scopery.modules.aiassistant.workspaceconfig.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface AiAssistantWorkspaceConfigRepository {

    Optional<AiAssistantWorkspaceConfig> findByWorkspaceId(UUID workspaceId);

    AiAssistantWorkspaceConfig save(AiAssistantWorkspaceConfig config);
}
