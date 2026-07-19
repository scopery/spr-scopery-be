package com.company.scopery.modules.servicesupport.warranty.application.response;
import com.company.scopery.modules.servicesupport.warranty.domain.model.WarrantyCoverage;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
public record WarrantyCoverageResponse(UUID id, UUID workspaceId, UUID projectId, UUID serviceProfileId,
        LocalDate startDate, LocalDate endDate, String status, Instant createdAt) {
    public static WarrantyCoverageResponse from(WarrantyCoverage w) {
        return new WarrantyCoverageResponse(w.id(), w.workspaceId(), w.projectId(), w.serviceProfileId(),
                w.startDate(), w.endDate(), w.status(), w.createdAt());
    }
}
