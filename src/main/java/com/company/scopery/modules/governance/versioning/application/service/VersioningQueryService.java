package com.company.scopery.modules.governance.versioning.application.service;
import com.company.scopery.modules.governance.shared.authorization.GovernanceAuthorizationService;
import com.company.scopery.modules.governance.shared.error.GovernanceExceptions;
import com.company.scopery.modules.governance.versioning.application.response.*;
import com.company.scopery.modules.governance.versioning.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.Objects; import java.util.UUID;
@Service
public class VersioningQueryService {
    private final GovernanceVersionRecordRepository versions; private final GovernanceSnapshotRepository snapshots;
    private final GovernanceAuthorizationService authorization;
    public VersioningQueryService(GovernanceVersionRecordRepository versions, GovernanceSnapshotRepository snapshots, GovernanceAuthorizationService authorization) {
        this.versions=versions; this.snapshots=snapshots; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<GovernanceVersionRecordResponse> listVersions(UUID projectId, String objectType, UUID targetId) {
        authorization.requireVersionView(projectId);
        return versions.findByTarget(objectType, targetId).stream().map(GovernanceVersionRecordResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public GovernanceSnapshotResponse getSnapshot(UUID projectId, UUID snapshotId) {
        authorization.requireVersionView(projectId);
        return GovernanceSnapshotResponse.from(snapshots.findById(snapshotId).orElseThrow(() -> GovernanceExceptions.snapshotNotFound(snapshotId)));
    }
    @Transactional(readOnly=true)
    public SnapshotDiffResponse diff(UUID projectId, UUID leftSnapshotId, UUID rightSnapshotId) {
        authorization.requireVersionView(projectId);
        var left = snapshots.findById(leftSnapshotId).orElseThrow(() -> GovernanceExceptions.snapshotNotFound(leftSnapshotId));
        var right = snapshots.findById(rightSnapshotId).orElseThrow(() -> GovernanceExceptions.snapshotNotFound(rightSnapshotId));
        boolean identical = Objects.equals(left.snapshotJson(), right.snapshotJson());
        return new SnapshotDiffResponse(left.id(), right.id(), identical, left.snapshotJson(), right.snapshotJson());
    }
}
