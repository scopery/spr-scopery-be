package com.company.scopery.modules.servicesupport.requesttype.application.response;
import com.company.scopery.modules.servicesupport.requesttype.domain.model.SupportRequestType;
import java.time.Instant; import java.util.UUID;
public record SupportRequestTypeResponse(UUID id, UUID workspaceId, String typeCode, String name,
        String defaultPriority, boolean portalVisible, boolean enabled, String status, Instant createdAt) {
    public static SupportRequestTypeResponse from(SupportRequestType d) {
        return new SupportRequestTypeResponse(d.id(), d.workspaceId(), d.typeCode(), d.name(),
                d.defaultPriority(), d.portalVisible(), d.enabled(), d.status(), d.createdAt());
    }
}
