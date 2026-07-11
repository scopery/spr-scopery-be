package com.company.scopery.modules.project.phasedefinition.domain.model;

import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionScope;
import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionStatus;

import java.time.Instant;
import java.util.UUID;

public record PhaseDefinition(
        UUID id,
        PhaseDefinitionScope scope,
        UUID workspaceId,
        String code,
        String name,
        String description,
        int displayOrder,
        boolean isSystemDefault,
        PhaseDefinitionStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static PhaseDefinition createSystem(
            String code,
            String name,
            String description,
            int displayOrder,
            boolean isSystemDefault) {
        return new PhaseDefinition(
                UUID.randomUUID(),
                PhaseDefinitionScope.SYSTEM,
                null,
                code,
                name,
                description,
                displayOrder,
                isSystemDefault,
                PhaseDefinitionStatus.ACTIVE,
                0,
                null,
                null
        );
    }

    public static PhaseDefinition createWorkspace(
            UUID workspaceId,
            String code,
            String name,
            String description,
            int displayOrder) {
        return new PhaseDefinition(
                UUID.randomUUID(),
                PhaseDefinitionScope.WORKSPACE,
                workspaceId,
                code,
                name,
                description,
                displayOrder,
                false,
                PhaseDefinitionStatus.ACTIVE,
                0,
                null,
                null
        );
    }

    public PhaseDefinition update(String name, String description, int displayOrder) {
        return new PhaseDefinition(
                this.id,
                this.scope,
                this.workspaceId,
                this.code,
                name,
                description,
                displayOrder,
                this.isSystemDefault,
                this.status,
                this.version,
                this.createdAt,
                this.updatedAt
        );
    }

    public PhaseDefinition archive() {
        return new PhaseDefinition(
                this.id,
                this.scope,
                this.workspaceId,
                this.code,
                this.name,
                this.description,
                this.displayOrder,
                this.isSystemDefault,
                PhaseDefinitionStatus.ARCHIVED,
                this.version,
                this.createdAt,
                this.updatedAt
        );
    }
}
