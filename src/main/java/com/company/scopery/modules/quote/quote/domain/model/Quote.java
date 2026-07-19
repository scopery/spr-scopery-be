package com.company.scopery.modules.quote.quote.domain.model;

import com.company.scopery.modules.quote.quote.domain.enums.QuoteStatus;

import java.time.Instant;
import java.util.UUID;

public record Quote(
        UUID id,
        UUID projectId,
        UUID workspaceId,
        UUID sourceFinanceScenarioId,
        String code,
        String title,
        String description,
        String clientName,
        String clientCompany,
        String clientEmail,
        String clientContactName,
        String clientReference,
        UUID externalPartyId,
        QuoteStatus status,
        UUID currentVersionId,
        Instant archivedAt,
        UUID archivedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static Quote create(
            UUID projectId,
            UUID workspaceId,
            UUID sourceFinanceScenarioId,
            String code,
            String title,
            String description,
            String clientName,
            String clientCompany,
            String clientEmail,
            String clientContactName,
            String clientReference) {
        return new Quote(
                UUID.randomUUID(), projectId, workspaceId, sourceFinanceScenarioId,
                code, title, description, clientName, clientCompany, clientEmail,
                clientContactName, clientReference, null, QuoteStatus.DRAFT, null,
                null, null, 0, null, null);
    }

    public Quote update(
            String title,
            String description,
            String clientName,
            String clientCompany,
            String clientEmail,
            String clientContactName,
            String clientReference) {
        return new Quote(
                id, projectId, workspaceId, sourceFinanceScenarioId, code, title, description,
                clientName, clientCompany, clientEmail, clientContactName, clientReference,
                externalPartyId, status, currentVersionId, archivedAt, archivedBy,
                version, createdAt, updatedAt);
    }

    public Quote withCurrentVersionId(UUID versionId) {
        return new Quote(
                id, projectId, workspaceId, sourceFinanceScenarioId, code, title, description,
                clientName, clientCompany, clientEmail, clientContactName, clientReference,
                externalPartyId, status == QuoteStatus.DRAFT ? QuoteStatus.ACTIVE : status,
                versionId, archivedAt, archivedBy, version, createdAt, updatedAt);
    }

    public Quote archive(UUID actorId) {
        return new Quote(
                id, projectId, workspaceId, sourceFinanceScenarioId, code, title, description,
                clientName, clientCompany, clientEmail, clientContactName, clientReference,
                externalPartyId, QuoteStatus.ARCHIVED, currentVersionId,
                Instant.now(), actorId, version, createdAt, updatedAt);
    }

    public boolean isArchived() {
        return status == QuoteStatus.ARCHIVED;
    }
}
