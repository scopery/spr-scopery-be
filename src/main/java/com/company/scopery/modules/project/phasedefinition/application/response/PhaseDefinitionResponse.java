package com.company.scopery.modules.project.phasedefinition.application.response;

import com.company.scopery.modules.project.phasedefinition.domain.model.PhaseDefinition;

import java.time.Instant;
import java.util.UUID;

public record PhaseDefinitionResponse(
        UUID id,
        String scope,
        UUID workspaceId,
        String code,
        String name,
        String description,
        int displayOrder,
        boolean isSystemDefault,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static PhaseDefinitionResponse from(PhaseDefinition pd) {
        return new PhaseDefinitionResponse(
                pd.id(),
                pd.scope() != null ? pd.scope().name() : null,
                pd.workspaceId(),
                pd.code(),
                pd.name(),
                pd.description(),
                pd.displayOrder(),
                pd.isSystemDefault(),
                pd.status() != null ? pd.status().name() : null,
                pd.version(),
                pd.createdAt(),
                pd.updatedAt()
        );
    }
}
