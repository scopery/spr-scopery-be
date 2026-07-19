package com.company.scopery.modules.governance.versioning.application.response;
import com.company.scopery.modules.governance.versioning.domain.model.GovernanceVersionRecord;
import java.util.UUID;
public record GovernanceVersionRecordResponse(UUID id, String objectTypeCode, UUID targetId, UUID snapshotId, int versionNumber, boolean currentFlag, boolean finalizedFlag) {
    public static GovernanceVersionRecordResponse from(GovernanceVersionRecord v) {
        return new GovernanceVersionRecordResponse(v.id(), v.objectTypeCode(), v.targetId(), v.snapshotId(), v.versionNumber(), v.currentFlag(), v.finalizedFlag());
    }
}
