package com.company.scopery.modules.servicesupport.sla.application.response;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaBreach;
import java.time.Instant; import java.util.UUID;
public record SlaBreachResponse(UUID id, UUID workspaceId, UUID supportCaseId, UUID slaClockId, String breachType,
        Instant breachedAt, String status) {
    public static SlaBreachResponse from(SlaBreach b) {
        return new SlaBreachResponse(b.id(), b.workspaceId(), b.supportCaseId(), b.slaClockId(), b.breachType(),
                b.breachedAt(), b.status());
    }
}
