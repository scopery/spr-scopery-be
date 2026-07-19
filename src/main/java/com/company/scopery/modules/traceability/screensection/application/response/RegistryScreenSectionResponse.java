package com.company.scopery.modules.traceability.screensection.application.response;
import com.company.scopery.modules.traceability.screensection.domain.model.RegistryScreenSection;
import java.time.Instant; import java.util.UUID;
public record RegistryScreenSectionResponse(UUID id, UUID screenId, UUID workspaceId, String name, String description,
                                            int displayOrder, String status, Instant createdAt) {
    public static RegistryScreenSectionResponse from(RegistryScreenSection e) {
        return new RegistryScreenSectionResponse(e.id(), e.screenId(), e.workspaceId(), e.name(), e.description(),
                e.displayOrder(), e.status().name(), e.createdAt());
    }
}
