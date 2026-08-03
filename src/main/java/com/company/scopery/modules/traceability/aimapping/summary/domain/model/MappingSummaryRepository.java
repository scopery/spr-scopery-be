package com.company.scopery.modules.traceability.aimapping.summary.domain.model;

import com.company.scopery.modules.traceability.aimapping.summary.domain.enums.SummaryEntityType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MappingSummaryRepository {

    MappingSummary save(MappingSummary summary);

    Optional<MappingSummary> findByEntityTypeAndEntityId(SummaryEntityType entityType, UUID entityId);

    List<MappingSummary> findByEntityTypeAndEntityIdIn(SummaryEntityType entityType, List<UUID> entityIds);

    void updateEmbedding(UUID id, float[] embedding);
}
