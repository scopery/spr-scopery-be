package com.company.scopery.modules.servicesupport.effort.application.response;
import com.company.scopery.modules.servicesupport.effort.domain.model.SupportEffortRecord;
import java.math.BigDecimal; import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
public record SupportEffortResponse(UUID id, UUID workspaceId, UUID supportCaseId, UUID resourceProfileId,
        BigDecimal effortHours, LocalDate effortDate, String status, Instant createdAt,
        boolean profitabilityRebuildRequested, boolean defaultHourlyCostAppliedOnRebuild) {
    public static SupportEffortResponse from(SupportEffortRecord e) {
        return new SupportEffortResponse(e.id(), e.workspaceId(), e.supportCaseId(), e.resourceProfileId(),
                e.effortHours(), e.effortDate(), e.status(), e.createdAt(), false, false);
    }
    public static SupportEffortResponse from(SupportEffortRecord e, boolean rebuildRequested) {
        return new SupportEffortResponse(e.id(), e.workspaceId(), e.supportCaseId(), e.resourceProfileId(),
                e.effortHours(), e.effortDate(), e.status(), e.createdAt(), rebuildRequested, rebuildRequested);
    }
}
