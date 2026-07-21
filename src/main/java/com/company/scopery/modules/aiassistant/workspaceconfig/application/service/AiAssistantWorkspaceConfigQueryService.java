package com.company.scopery.modules.aiassistant.workspaceconfig.application.service;

import com.company.scopery.modules.aiassistant.shared.error.AiAssistantExceptions;
import com.company.scopery.modules.aiassistant.workspaceconfig.application.response.AiAssistantWorkspaceConfigResponse;
import com.company.scopery.modules.aiassistant.workspaceconfig.domain.model.AiAssistantWorkspaceConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class AiAssistantWorkspaceConfigQueryService {

    private final AiAssistantWorkspaceConfigRepository configRepository;

    public AiAssistantWorkspaceConfigQueryService(AiAssistantWorkspaceConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Transactional(readOnly = true)
    public Optional<AiAssistantWorkspaceConfigResponse> findByWorkspaceId(UUID workspaceId) {
        return configRepository.findByWorkspaceId(workspaceId)
                .map(AiAssistantWorkspaceConfigResponse::from);
    }

    @Transactional(readOnly = true)
    public AiAssistantWorkspaceConfigResponse getByWorkspaceId(UUID workspaceId) {
        return configRepository.findByWorkspaceId(workspaceId)
                .map(AiAssistantWorkspaceConfigResponse::from)
                .orElseThrow(() -> AiAssistantExceptions.workspaceConfigNotFound(workspaceId));
    }
}
