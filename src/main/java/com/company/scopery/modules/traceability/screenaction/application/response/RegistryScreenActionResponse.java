package com.company.scopery.modules.traceability.screenaction.application.response;
import com.company.scopery.modules.traceability.screenaction.domain.model.RegistryScreenAction;
import java.time.Instant; import java.util.UUID;
public record RegistryScreenActionResponse(UUID id, UUID screenId, UUID workspaceId, String actionCode, String name,
                                           String actionType, String description, int displayOrder,
                                           String status, Instant createdAt) {
    public static RegistryScreenActionResponse from(RegistryScreenAction e) {
        return new RegistryScreenActionResponse(e.id(), e.screenId(), e.workspaceId(), e.actionCode(), e.name(),
                e.actionType(), e.description(), e.displayOrder(), e.status().name(), e.createdAt());
    }
}
