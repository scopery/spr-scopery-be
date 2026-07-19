package com.company.scopery.modules.governance.report.application.service;
import com.company.scopery.modules.governance.grant.domain.model.ObjectAccessGrantRepository;
import com.company.scopery.modules.governance.lock.domain.model.ObjectLockRepository;
import com.company.scopery.modules.governance.ownership.domain.model.ObjectOwnershipRepository;
import com.company.scopery.modules.governance.report.application.response.*;
import com.company.scopery.modules.governance.shared.authorization.GovernanceAuthorizationService;
import com.company.scopery.modules.governance.versioning.domain.model.GovernanceVersionRecordRepository;
import com.company.scopery.modules.governance.versioning.domain.model.RestoreRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class GovernanceReportQueryService {
    private final ObjectOwnershipRepository ownerships;
    private final ObjectLockRepository locks;
    private final ObjectAccessGrantRepository grants;
    private final GovernanceVersionRecordRepository versions;
    private final RestoreRequestRepository restores;
    private final GovernanceAuthorizationService authorization;

    public GovernanceReportQueryService(ObjectOwnershipRepository ownerships, ObjectLockRepository locks,
                                        ObjectAccessGrantRepository grants, GovernanceVersionRecordRepository versions,
                                        RestoreRequestRepository restores, GovernanceAuthorizationService authorization) {
        this.ownerships = ownerships; this.locks = locks; this.grants = grants;
        this.versions = versions; this.restores = restores; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public GovernanceOwnershipReportResponse ownershipReport(UUID projectId) {
        authorization.requireReportView(projectId);
        return GovernanceOwnershipReportResponse.from(projectId, ownerships.findByProjectId(projectId));
    }

    @Transactional(readOnly = true)
    public GovernanceAccessGrantReportResponse accessGrantReport(UUID projectId) {
        authorization.requireReportView(projectId);
        return GovernanceAccessGrantReportResponse.from(projectId, grants.findByProjectId(projectId));
    }

    @Transactional(readOnly = true)
    public GovernanceVersionHistoryReportResponse versionHistoryReport(UUID projectId) {
        authorization.requireReportView(projectId);
        return GovernanceVersionHistoryReportResponse.from(projectId, versions.findByProjectId(projectId));
    }

    @Transactional(readOnly = true)
    public GovernanceLockedObjectsReportResponse lockedObjectsReport(UUID projectId) {
        authorization.requireReportView(projectId);
        return GovernanceLockedObjectsReportResponse.from(projectId, locks.findByProjectId(projectId));
    }

    @Transactional(readOnly = true)
    public GovernanceRestoreActivityReportResponse restoreActivityReport(UUID projectId) {
        authorization.requireReportView(projectId);
        return GovernanceRestoreActivityReportResponse.from(projectId, restores.findByProjectId(projectId));
    }

    @Transactional(readOnly = true)
    public GovernanceReportPackResponse pack(UUID projectId) {
        authorization.requireReportView(projectId);
        var ownershipList = ownerships.findByProjectId(projectId);
        var ownershipRows = ownershipList.stream()
                .map(o -> new GovernanceReportPackResponse.OwnershipRow(o.objectTypeCode(), o.targetId(), o.ownerTargetType(), o.ownerTargetId(), o.status()))
                .toList();
        int activeLocks = (int) locks.findByProjectId(projectId).stream().filter(l -> "ACTIVE".equals(l.status())).count();
        int grantCount = (int) grants.findByProjectId(projectId).stream().filter(g -> "ACTIVE".equals(g.status())).count();
        return new GovernanceReportPackResponse(projectId, ownershipRows.size(), ownershipRows, activeLocks, grantCount);
    }
}
