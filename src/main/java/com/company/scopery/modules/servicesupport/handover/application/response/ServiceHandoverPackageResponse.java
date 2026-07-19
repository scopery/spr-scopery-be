package com.company.scopery.modules.servicesupport.handover.application.response;
import com.company.scopery.modules.servicesupport.handover.domain.model.ServiceHandoverPackage;
import java.time.Instant; import java.util.UUID;
public record ServiceHandoverPackageResponse(UUID id, UUID workspaceId, UUID projectId, String packageCode,
        String title, String status, boolean clientVisible, Instant finalizedAt, Instant createdAt) {
    public static ServiceHandoverPackageResponse from(ServiceHandoverPackage d) {
        return new ServiceHandoverPackageResponse(d.id(), d.workspaceId(), d.projectId(), d.packageCode(),
                d.title(), d.status(), d.clientVisible(), d.finalizedAt(), d.createdAt());
    }
}
