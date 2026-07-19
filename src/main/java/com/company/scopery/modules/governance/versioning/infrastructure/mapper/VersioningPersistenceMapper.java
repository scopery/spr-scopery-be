package com.company.scopery.modules.governance.versioning.infrastructure.mapper;
import com.company.scopery.modules.governance.versioning.domain.model.*;
import com.company.scopery.modules.governance.versioning.infrastructure.persistence.*;
import org.springframework.stereotype.Component;
@Component
public class VersioningPersistenceMapper {
    public GovernanceVersionRecord toDomain(GovernanceVersionRecordJpaEntity e) {
        return new GovernanceVersionRecord(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getObjectTypeCode(), e.getTargetId(),
                e.getDomainVersionType(), e.getDomainVersionId(), e.getSnapshotId(), e.getChangeType(), e.getChangeReason(),
                e.isCurrentFlag(), e.isFinalizedFlag(), e.isRestoreEligible(), e.getVersionNumber(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public GovernanceVersionRecordJpaEntity toJpa(GovernanceVersionRecord d) {
        GovernanceVersionRecordJpaEntity e = new GovernanceVersionRecordJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setObjectTypeCode(d.objectTypeCode());
        e.setTargetId(d.targetId()); e.setDomainVersionType(d.domainVersionType()); e.setDomainVersionId(d.domainVersionId());
        e.setSnapshotId(d.snapshotId()); e.setChangeType(d.changeType()); e.setChangeReason(d.changeReason());
        e.setCurrentFlag(d.currentFlag()); e.setFinalizedFlag(d.finalizedFlag()); e.setRestoreEligible(d.restoreEligible());
        e.setVersionNumber(d.versionNumber()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
    public GovernanceSnapshot toDomain(GovernanceSnapshotJpaEntity e) {
        return new GovernanceSnapshot(e.getId(), e.getWorkspaceId(), e.getObjectTypeCode(), e.getTargetId(), e.getSnapshotMode(),
                e.getSchemaVersion(), e.getSnapshotJson(), e.getMaskedFieldsJson(), e.isSensitiveFieldsPresent(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public GovernanceSnapshotJpaEntity toJpa(GovernanceSnapshot d) {
        GovernanceSnapshotJpaEntity e = new GovernanceSnapshotJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setObjectTypeCode(d.objectTypeCode()); e.setTargetId(d.targetId());
        e.setSnapshotMode(d.snapshotMode()); e.setSchemaVersion(d.schemaVersion()); e.setSnapshotJson(d.snapshotJson());
        e.setMaskedFieldsJson(d.maskedFieldsJson()); e.setSensitiveFieldsPresent(d.sensitiveFieldsPresent()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
    public RestoreRequest toDomain(RestoreRequestJpaEntity e) {
        return new RestoreRequest(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getObjectTypeCode(), e.getTargetId(),
                e.getRestoreFromVersionRecordId(), e.getStatus(), e.getReason(), e.getCompletedAt(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public RestoreRequestJpaEntity toJpa(RestoreRequest d) {
        RestoreRequestJpaEntity e = new RestoreRequestJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setObjectTypeCode(d.objectTypeCode());
        e.setTargetId(d.targetId()); e.setRestoreFromVersionRecordId(d.restoreFromVersionRecordId()); e.setStatus(d.status());
        e.setReason(d.reason()); e.setCompletedAt(d.completedAt()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
