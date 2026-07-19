package com.company.scopery.modules.quote.quote.application.response;

import com.company.scopery.modules.quote.quote.domain.model.Quote;

import java.time.Instant;
import java.util.UUID;

public record QuoteResponse(
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
        String status,
        UUID currentVersionId,
        Instant archivedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static QuoteResponse from(Quote q) {
        return new QuoteResponse(
                q.id(), q.projectId(), q.workspaceId(), q.sourceFinanceScenarioId(),
                q.code(), q.title(), q.description(), q.clientName(), q.clientCompany(),
                q.clientEmail(), q.clientContactName(), q.clientReference(),
                q.status().name(), q.currentVersionId(), q.archivedAt(),
                q.createdAt(), q.updatedAt());
    }
}
