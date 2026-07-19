package com.company.scopery.modules.scope.evidence.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface AcceptanceEvidenceRepository {
    AcceptanceEvidence save(AcceptanceEvidence evidence);
    Optional<AcceptanceEvidence> findByIdAndProjectId(UUID id, UUID projectId);
    List<AcceptanceEvidence> findByDeliverableId(UUID deliverableId);
    long countByProjectId(UUID projectId);
}
