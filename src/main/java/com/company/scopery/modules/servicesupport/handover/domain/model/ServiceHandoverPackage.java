package com.company.scopery.modules.servicesupport.handover.domain.model;
import java.time.Instant; import java.util.UUID;
public record ServiceHandoverPackage(UUID id, UUID workspaceId, UUID projectId, UUID serviceProfileId,
        String packageCode, String title, String summary, String status, boolean clientVisible,
        Instant finalizedAt, UUID finalizedBy, int version, Instant createdAt, Instant updatedAt) {
    public static ServiceHandoverPackage create(UUID workspaceId, UUID projectId, String title) {
        Instant now = Instant.now();
        String code = "HOP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new ServiceHandoverPackage(UUID.randomUUID(), workspaceId, projectId, null, code, title, null,
                "DRAFT", false, null, null, 0, now, now);
    }
    public ServiceHandoverPackage finalizePackage(UUID finalizer) {
        if ("FINALIZED".equals(status)) throw new IllegalStateException("Already finalized");
        Instant now = Instant.now();
        return new ServiceHandoverPackage(id, workspaceId, projectId, serviceProfileId, packageCode, title, summary,
                "FINALIZED", clientVisible, now, finalizer, version, createdAt, now);
    }
}
