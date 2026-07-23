package com.company.scopery.modules.traceability.functionalitem.domain.model;

import com.company.scopery.modules.traceability.functionalitem.domain.enums.FunctionalItemPriority;
import com.company.scopery.modules.traceability.functionalitem.domain.enums.FunctionalItemStatus;
import com.company.scopery.modules.traceability.functionalitem.domain.enums.FunctionalItemType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FunctionalItem(
        UUID id,
        UUID projectId,
        UUID workspaceId,
        UUID moduleId,
        String code,
        String title,
        String description,
        FunctionalItemPriority priority,
        FunctionalItemStatus status,
        FunctionalItemType type,
        List<String> acceptanceCriteria,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static FunctionalItem create(
            UUID projectId,
            UUID workspaceId,
            UUID moduleId,
            String code,
            String title,
            String description,
            FunctionalItemPriority priority,
            FunctionalItemType type,
            List<String> acceptanceCriteria
    ) {
        // Leave createdAt/updatedAt null so AuditableJpaEntity.isNew() stays true on first persist.
        return new FunctionalItem(
                UUID.randomUUID(),
                projectId,
                workspaceId,
                moduleId,
                code,
                title,
                description,
                priority,
                FunctionalItemStatus.DRAFT,
                type,
                acceptanceCriteria,
                0,
                null,
                null
        );
    }

    public FunctionalItem withUpdated(
            UUID moduleId,
            String title,
            String description,
            FunctionalItemPriority priority,
            FunctionalItemStatus status,
            FunctionalItemType type,
            List<String> acceptanceCriteria
    ) {
        return new FunctionalItem(
                this.id,
                this.projectId,
                this.workspaceId,
                moduleId != null ? moduleId : this.moduleId,
                this.code,
                title,
                description,
                priority,
                status,
                type,
                acceptanceCriteria,
                this.version,
                this.createdAt,
                Instant.now()
        );
    }
}
