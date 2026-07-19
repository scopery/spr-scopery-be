package com.company.scopery.modules.servicesupport.warranty.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record WarrantyCoverage(UUID id, UUID workspaceId, UUID projectId, UUID serviceProfileId,
        LocalDate startDate, LocalDate endDate, String status, Instant createdAt) {
    public static WarrantyCoverage create(UUID workspaceId, UUID projectId, UUID serviceProfileId,
                                          LocalDate startDate, LocalDate endDate) {
        if (workspaceId == null) throw new IllegalArgumentException("workspaceId required");
        if (startDate == null) throw new IllegalArgumentException("startDate required");
        return new WarrantyCoverage(UUID.randomUUID(), workspaceId, projectId, serviceProfileId,
                startDate, endDate, "ACTIVE", Instant.now());
    }
    public WarrantyCoverage expire() {
        return new WarrantyCoverage(id, workspaceId, projectId, serviceProfileId, startDate, endDate, "EXPIRED", createdAt);
    }
    public boolean covers(LocalDate day) {
        if (!"ACTIVE".equals(status) || day == null) return false;
        if (day.isBefore(startDate)) return false;
        return endDate == null || !day.isAfter(endDate);
    }
}
