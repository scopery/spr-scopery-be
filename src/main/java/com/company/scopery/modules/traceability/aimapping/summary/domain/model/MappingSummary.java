package com.company.scopery.modules.traceability.aimapping.summary.domain.model;

import com.company.scopery.modules.traceability.aimapping.summary.domain.enums.SummaryEntityType;

import java.time.Instant;
import java.util.UUID;

public record MappingSummary(
        UUID id,
        SummaryEntityType entityType,
        UUID entityId,
        int entityVersion,
        String compactText,
        String structuredJson,
        String summaryHash,
        Instant generatedAt
) {
    public static MappingSummary create(SummaryEntityType entityType, UUID entityId,
                                        int entityVersion, String compactText,
                                        String structuredJson, String summaryHash) {
        return new MappingSummary(
                UUID.randomUUID(),
                entityType,
                entityId,
                entityVersion,
                compactText,
                structuredJson,
                summaryHash,
                Instant.now()
        );
    }
}
