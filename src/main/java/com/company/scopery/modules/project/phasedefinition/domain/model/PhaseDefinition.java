package com.company.scopery.modules.project.phasedefinition.domain.model;

import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionScope;
import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionStatus;

import java.time.Instant;
import java.util.UUID;

public record PhaseDefinition(
        UUID id,
        PhaseDefinitionScope scope,
        UUID organizationId,
        UUID workspaceId,
        String code,
        String name,
        String description,
        int displayOrder,
        /**
         * Built-in / seed catalog flag ({@code is_system_default}).
         * Built-in definitions cannot be hard-deleted or archived.
         */
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

    public static PhaseDefinition createOrganization(
            UUID organizationId,
            String code,
            String name,
            String description,
            int displayOrder) {
        return new PhaseDefinition(
                UUID.randomUUID(),
                PhaseDefinitionScope.ORGANIZATION,
                organizationId,
                null,
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

    public static PhaseDefinition createWorkspace(
            UUID workspaceId,
            String code,
            String name,
            String description,
            int displayOrder) {
        return new PhaseDefinition(
                UUID.randomUUID(),
                PhaseDefinitionScope.WORKSPACE,
                null,
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
                this.organizationId,
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

    public PhaseDefinition activate() {
        return new PhaseDefinition(
                this.id,
                this.scope,
                this.organizationId,
                this.workspaceId,
                this.code,
                this.name,
                this.description,
                this.displayOrder,
                this.isSystemDefault,
                PhaseDefinitionStatus.ACTIVE,
                this.version,
                this.createdAt,
                this.updatedAt
        );
    }

    public PhaseDefinition deactivate() {
        return new PhaseDefinition(
                this.id,
                this.scope,
                this.organizationId,
                this.workspaceId,
                this.code,
                this.name,
                this.description,
                this.displayOrder,
                this.isSystemDefault,
                PhaseDefinitionStatus.INACTIVE,
                this.version,
                this.createdAt,
                this.updatedAt
        );
    }

    public PhaseDefinition archive() {
        return new PhaseDefinition(
                this.id,
                this.scope,
                this.organizationId,
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
