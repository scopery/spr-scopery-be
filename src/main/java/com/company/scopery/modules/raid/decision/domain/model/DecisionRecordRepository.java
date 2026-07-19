package com.company.scopery.modules.raid.decision.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface DecisionRecordRepository {
    DecisionRecord save(DecisionRecord d);
    Optional<DecisionRecord> findByIdAndProjectId(UUID id, UUID projectId);
    List<DecisionRecord> findByProjectId(UUID projectId);
}
