package com.company.scopery.modules.governance.report.application.response;
import com.company.scopery.modules.governance.ownership.domain.model.ObjectOwnership;
import java.time.Instant; import java.util.List; import java.util.UUID;
public record GovernanceOwnershipReportResponse(UUID projectId, int totalCount, int activeCount, List<OwnershipRow> rows) {
    public record OwnershipRow(UUID id, String objectTypeCode, UUID targetId, String ownerTargetType, UUID ownerTargetId, String status, Instant assignedAt) {}
    public static GovernanceOwnershipReportResponse from(UUID projectId, List<ObjectOwnership> list) {
        var rows = list.stream().map(o -> new OwnershipRow(o.id(), o.objectTypeCode(), o.targetId(), o.ownerTargetType(), o.ownerTargetId(), o.status(), o.assignedAt())).toList();
        int active = (int) list.stream().filter(o -> "ACTIVE".equals(o.status())).count();
        return new GovernanceOwnershipReportResponse(projectId, rows.size(), active, rows);
    }
}
