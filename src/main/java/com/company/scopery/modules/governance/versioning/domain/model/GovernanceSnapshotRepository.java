package com.company.scopery.modules.governance.versioning.domain.model;
import java.util.*; import java.util.UUID;
public interface GovernanceSnapshotRepository {
    GovernanceSnapshot save(GovernanceSnapshot s);
    Optional<GovernanceSnapshot> findById(UUID id);
}
