package com.company.scopery.modules.governance.report.application.response;
import com.company.scopery.modules.governance.versioning.domain.model.GovernanceVersionRecord;
import java.time.Instant; import java.util.List; import java.util.UUID;
public record GovernanceVersionHistoryReportResponse(UUID projectId, int totalCount, List<VersionRow> rows) {
    public record VersionRow(UUID id, String objectTypeCode, UUID targetId, String changeType, String changeReason, int versionNumber, boolean currentFlag, boolean finalizedFlag, boolean restoreEligible, Instant createdAt) {}
    public static GovernanceVersionHistoryReportResponse from(UUID projectId, List<GovernanceVersionRecord> list) {
        var rows = list.stream().map(v -> new VersionRow(v.id(), v.objectTypeCode(), v.targetId(), v.changeType(), v.changeReason(), v.versionNumber(), v.currentFlag(), v.finalizedFlag(), v.restoreEligible(), v.createdAt())).toList();
        return new GovernanceVersionHistoryReportResponse(projectId, rows.size(), rows);
    }
}
