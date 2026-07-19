package com.company.scopery.modules.servicesupport.effort.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Support effort is operational tracking only — not payroll / timesheet. */
public record SupportEffortRecord(UUID id, UUID workspaceId, UUID supportCaseId, UUID resourceProfileId,
        BigDecimal effortHours, LocalDate effortDate, String status, Instant createdAt) {
    public static SupportEffortRecord create(UUID workspaceId, UUID supportCaseId, UUID resourceProfileId,
                                             BigDecimal hours, LocalDate effortDate) {
        if (workspaceId == null || supportCaseId == null) throw new IllegalArgumentException("ids required");
        if (hours == null || hours.signum() <= 0) throw new IllegalArgumentException("effortHours must be > 0");
        return new SupportEffortRecord(UUID.randomUUID(), workspaceId, supportCaseId, resourceProfileId,
                hours, effortDate != null ? effortDate : LocalDate.now(), "RECORDED", Instant.now());
    }
    public SupportEffortRecord cancel() {
        return new SupportEffortRecord(id, workspaceId, supportCaseId, resourceProfileId, effortHours, effortDate, "CANCELLED", createdAt);
    }
}
