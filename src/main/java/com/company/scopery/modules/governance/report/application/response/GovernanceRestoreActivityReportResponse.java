package com.company.scopery.modules.governance.report.application.response;
import com.company.scopery.modules.governance.versioning.domain.model.RestoreRequest;
import java.time.Instant; import java.util.List; import java.util.UUID;
public record GovernanceRestoreActivityReportResponse(UUID projectId, int totalCount, List<RestoreRow> rows) {
    public record RestoreRow(UUID id, String objectTypeCode, UUID targetId, UUID restoreFromVersionRecordId, String status, String reason, Instant completedAt, Instant createdAt) {}
    public static GovernanceRestoreActivityReportResponse from(UUID projectId, List<RestoreRequest> list) {
        var rows = list.stream().map(r -> new RestoreRow(r.id(), r.objectTypeCode(), r.targetId(), r.restoreFromVersionRecordId(), r.status(), r.reason(), r.completedAt(), r.createdAt())).toList();
        return new GovernanceRestoreActivityReportResponse(projectId, rows.size(), rows);
    }
}
