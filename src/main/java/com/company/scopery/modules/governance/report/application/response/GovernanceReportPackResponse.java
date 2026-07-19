package com.company.scopery.modules.governance.report.application.response;
import java.util.List; import java.util.UUID;
public record GovernanceReportPackResponse(
        UUID projectId,
        int ownershipCount,
        List<OwnershipRow> ownerships,
        int activeLockCount,
        int accessGrantSampleCount
) {
    public record OwnershipRow(String objectTypeCode, UUID targetId, String ownerTargetType, UUID ownerTargetId, String status) {}
}
