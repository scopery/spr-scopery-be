package com.company.scopery.modules.governance.report.application.response;
import com.company.scopery.modules.governance.grant.domain.model.ObjectAccessGrant;
import java.time.Instant; import java.util.List; import java.util.UUID;
public record GovernanceAccessGrantReportResponse(UUID projectId, int totalCount, int activeCount, List<GrantRow> rows) {
    public record GrantRow(UUID id, String objectTypeCode, UUID targetId, String granteeType, UUID granteeId, String grantRole, String status, Instant expiresAt) {}
    public static GovernanceAccessGrantReportResponse from(UUID projectId, List<ObjectAccessGrant> list) {
        var rows = list.stream().map(g -> new GrantRow(g.id(), g.objectTypeCode(), g.targetId(), g.granteeType(), g.granteeId(), g.grantRole(), g.status(), g.expiresAt())).toList();
        int active = (int) list.stream().filter(g -> "ACTIVE".equals(g.status())).count();
        return new GovernanceAccessGrantReportResponse(projectId, rows.size(), active, rows);
    }
}
