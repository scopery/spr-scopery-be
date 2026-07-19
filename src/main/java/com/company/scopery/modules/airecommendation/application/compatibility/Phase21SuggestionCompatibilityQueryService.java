package com.company.scopery.modules.airecommendation.application.compatibility;

import com.company.scopery.modules.airecommendation.application.port.Phase21SuggestionCompatibilityPort;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestion;
import com.company.scopery.modules.airecommendation.shared.config.AiRecommendationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class Phase21SuggestionCompatibilityQueryService {

    private final Phase21SuggestionCompatibilityPort compatibilityPort;
    private final AiRecommendationProperties properties;

    public Phase21SuggestionCompatibilityQueryService(Phase21SuggestionCompatibilityPort compatibilityPort,
                                                       AiRecommendationProperties properties) {
        this.compatibilityPort = compatibilityPort;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public Page<AiSuggestion> listForProject(UUID projectId, UUID workspaceId, Pageable pageable) {
        if (!properties.isPhase21ReadCompatEnabled()) {
            return Page.empty(pageable);
        }
        return compatibilityPort.listForProject(projectId, workspaceId, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<AiSuggestion> getByLegacyId(UUID phase21Id) {
        if (!properties.isPhase21ReadCompatEnabled()) {
            return Optional.empty();
        }
        return compatibilityPort.getByLegacyId(phase21Id, null);
    }
}
