package com.company.scopery.modules.traceability.nonfunctionalitem.domain.model;

import com.company.scopery.modules.traceability.nonfunctionalitem.domain.enums.NfrCategory;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.enums.NfrPriority;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.enums.NfrScopeType;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.enums.NfrStatus;

import java.time.Instant;
import java.util.UUID;

public record NonFunctionalItem(
        UUID id,
        UUID projectId,
        UUID workspaceId,
        String code,
        String title,
        String description,
        NfrCategory category,
        NfrPriority priority,
        NfrStatus status,
        String targetMetric,
        NfrScopeType scopeType,
        UUID scopeRefId,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static NonFunctionalItem create(
            UUID projectId,
            UUID workspaceId,
            String code,
            String title,
            String description,
            NfrCategory category,
            NfrPriority priority,
            String targetMetric,
            NfrScopeType scopeType,
            UUID scopeRefId
    ) {
        // Leave createdAt/updatedAt null so AuditableJpaEntity.isNew() stays true on first persist.
        return new NonFunctionalItem(
                UUID.randomUUID(),
                projectId,
                workspaceId,
                code,
                title,
                description,
                category,
                priority,
                NfrStatus.DRAFT,
                targetMetric,
                scopeType,
                scopeRefId,
                0,
                null,
                null
        );
    }

    public NonFunctionalItem withUpdated(
            String title,
            String description,
            NfrCategory category,
            NfrPriority priority,
            NfrStatus status,
            String targetMetric,
            NfrScopeType scopeType,
            UUID scopeRefId
    ) {
        return new NonFunctionalItem(
                this.id,
                this.projectId,
                this.workspaceId,
                this.code,
                title,
                description,
                category,
                priority,
                status,
                targetMetric,
                scopeType,
                scopeRefId,
                this.version,
                this.createdAt,
                Instant.now()
        );
    }
}
