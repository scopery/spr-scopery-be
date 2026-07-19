package com.company.scopery.modules.servicesupport.serviceprofile.application.response;
import com.company.scopery.modules.servicesupport.serviceprofile.domain.model.ServiceProfile;
import java.time.Instant; import java.util.UUID;
public record ServiceProfileResponse(UUID id, UUID workspaceId, String scopeType, UUID projectId,
        boolean portalIntakeEnabled, String status, Instant createdAt) {
    public static ServiceProfileResponse from(ServiceProfile d) {
        return new ServiceProfileResponse(d.id(), d.workspaceId(), d.scopeType(), d.projectId(),
                d.portalIntakeEnabled(), d.status(), d.createdAt());
    }
}
