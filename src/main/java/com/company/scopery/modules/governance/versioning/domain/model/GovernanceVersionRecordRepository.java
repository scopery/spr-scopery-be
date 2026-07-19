package com.company.scopery.modules.governance.versioning.domain.model;
import java.util.*; import java.util.UUID;
public interface GovernanceVersionRecordRepository {
    GovernanceVersionRecord save(GovernanceVersionRecord v);
    Optional<GovernanceVersionRecord> findById(UUID id);
    List<GovernanceVersionRecord> findByTarget(String objectTypeCode, UUID targetId);
    Optional<GovernanceVersionRecord> findCurrent(String objectTypeCode, UUID targetId);
    List<GovernanceVersionRecord> findByProjectId(UUID projectId);
}
