package com.company.scopery.modules.governance.versioning.application.response;
import com.company.scopery.modules.governance.versioning.domain.model.GovernanceSnapshot;
import java.util.UUID;
public record GovernanceSnapshotResponse(UUID id, String objectTypeCode, UUID targetId, String snapshotJson) {
    public static GovernanceSnapshotResponse from(GovernanceSnapshot s) { return new GovernanceSnapshotResponse(s.id(), s.objectTypeCode(), s.targetId(), s.snapshotJson()); }
}
