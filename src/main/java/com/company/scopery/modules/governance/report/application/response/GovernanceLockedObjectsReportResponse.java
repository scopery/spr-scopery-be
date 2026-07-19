package com.company.scopery.modules.governance.report.application.response;
import com.company.scopery.modules.governance.lock.domain.model.ObjectLock;
import java.time.Instant; import java.util.List; import java.util.UUID;
public record GovernanceLockedObjectsReportResponse(UUID projectId, int totalCount, int activeCount, List<LockRow> rows) {
    public record LockRow(UUID id, String objectTypeCode, UUID targetId, String lockType, String status, String reason, Instant lockedAt, Instant releasedAt) {}
    public static GovernanceLockedObjectsReportResponse from(UUID projectId, List<ObjectLock> list) {
        var rows = list.stream().map(l -> new LockRow(l.id(), l.objectTypeCode(), l.targetId(), l.lockType(), l.status(), l.reason(), l.lockedAt(), l.releasedAt())).toList();
        int active = (int) list.stream().filter(l -> "ACTIVE".equals(l.status())).count();
        return new GovernanceLockedObjectsReportResponse(projectId, rows.size(), active, rows);
    }
}
