package com.company.scopery.modules.servicesupport.sla.application.response;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaClock;
import com.company.scopery.modules.servicesupport.sla.domain.service.SlaClockService;
import java.time.Instant; import java.util.UUID;
public record SlaClockResponse(UUID id, UUID workspaceId, UUID supportCaseId, String clockType, Instant startedAt,
        Instant dueAt, String status, boolean breached, boolean atRisk) {
    public static SlaClockResponse from(SlaClock d) {
        Instant now = Instant.now();
        boolean breached = SlaClockService.isBreached(d.dueAt(), now);
        boolean atRisk = SlaClockService.isAtRisk(d.dueAt(), now, 30);
        return new SlaClockResponse(d.id(), d.workspaceId(), d.supportCaseId(), d.clockType(), d.startedAt(),
                d.dueAt(), d.status(), breached, atRisk);
    }
}
