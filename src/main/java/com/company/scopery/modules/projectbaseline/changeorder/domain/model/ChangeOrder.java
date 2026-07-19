package com.company.scopery.modules.projectbaseline.changeorder.domain.model;

import com.company.scopery.modules.projectbaseline.changeorder.domain.enums.ChangeOrderStatus;

import java.time.Instant;
import java.util.UUID;

public record ChangeOrder(
        UUID id,
        UUID changeRequestId,
        UUID projectId,
        UUID workspaceId,
        String code,
        String title,
        String description,
        ChangeOrderStatus status,
        String commercialImpactJson,
        UUID sourceQuoteVersionId,
        UUID futureContractId,
        Instant approvedAt,
        UUID approvedBy,
        Instant rejectedAt,
        UUID rejectedBy,
        String rejectionReason,
        Instant archivedAt,
        UUID archivedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ChangeOrder create(
            UUID changeRequestId, UUID projectId, UUID workspaceId, String code, String title,
            String description, String commercialImpactJson, UUID sourceQuoteVersionId) {
        return new ChangeOrder(UUID.randomUUID(), changeRequestId, projectId, workspaceId, code, title,
                description, ChangeOrderStatus.DRAFT, commercialImpactJson, sourceQuoteVersionId, null,
                null, null, null, null, null, null, null, 0, null, null);
    }

    public boolean isDraft() { return status == ChangeOrderStatus.DRAFT; }

    public ChangeOrder updateDraft(String title, String description, String commercialImpactJson,
                                   UUID sourceQuoteVersionId) {
        return new ChangeOrder(id, changeRequestId, projectId, workspaceId, code, title, description, status,
                commercialImpactJson, sourceQuoteVersionId, futureContractId, approvedAt, approvedBy,
                rejectedAt, rejectedBy, rejectionReason, archivedAt, archivedBy, version, createdAt, updatedAt);
    }

    public ChangeOrder approve(UUID actorId) {
        return new ChangeOrder(id, changeRequestId, projectId, workspaceId, code, title, description,
                ChangeOrderStatus.APPROVED, commercialImpactJson, sourceQuoteVersionId, futureContractId,
                Instant.now(), actorId, rejectedAt, rejectedBy, rejectionReason, archivedAt, archivedBy,
                version, createdAt, updatedAt);
    }

    public ChangeOrder reject(UUID actorId, String rejectionReason) {
        return new ChangeOrder(id, changeRequestId, projectId, workspaceId, code, title, description,
                ChangeOrderStatus.REJECTED, commercialImpactJson, sourceQuoteVersionId, futureContractId,
                approvedAt, approvedBy, Instant.now(), actorId, rejectionReason, archivedAt, archivedBy,
                version, createdAt, updatedAt);
    }

    public ChangeOrder archive(UUID actorId) {
        return new ChangeOrder(id, changeRequestId, projectId, workspaceId, code, title, description,
                ChangeOrderStatus.ARCHIVED, commercialImpactJson, sourceQuoteVersionId, futureContractId,
                approvedAt, approvedBy, rejectedAt, rejectedBy, rejectionReason, Instant.now(), actorId,
                version, createdAt, updatedAt);
    }
}
