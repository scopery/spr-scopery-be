package com.company.scopery.modules.traceability.appcomponent.application.action;

import com.company.scopery.modules.knowledge.shared.storage.ObjectStorageProvider;
import com.company.scopery.modules.traceability.appcomponent.application.command.RequestComponentScreenshotUploadCommand;
import com.company.scopery.modules.traceability.appcomponent.application.response.ComponentScreenshotUploadResponse;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponentRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityStorageLocations;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Component
public class RequestComponentScreenshotUploadAction {

    private final RegistryAppComponentRepository componentRepo;
    private final ObjectStorageProvider storageProvider;
    private final TraceabilityAuthorizationService authorization;

    public RequestComponentScreenshotUploadAction(RegistryAppComponentRepository componentRepo,
                                                   ObjectStorageProvider storageProvider,
                                                   TraceabilityAuthorizationService authorization) {
        this.componentRepo = componentRepo;
        this.storageProvider = storageProvider;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public ComponentScreenshotUploadResponse execute(RequestComponentScreenshotUploadCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        componentRepo.findById(c.componentId())
                .orElseThrow(() -> TraceabilityExceptions.appComponentNotFound(c.componentId()));

        var location = TraceabilityStorageLocations.COMPONENT_SCREENSHOT;
        String objectKey = location.keyFor(c.componentId(), UUID.randomUUID() + extensionFor(c.contentType()));
        var upload = storageProvider.createPresignedUpload(objectKey, c.contentType(), location.maxSizeBytes());
        return new ComponentScreenshotUploadResponse(c.componentId(), upload.uploadUrl(), objectKey, upload.expiresAt(), location.maxSizeBytes());
    }

    private String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png"  -> ".png";
            case "image/gif"  -> ".gif";
            case "image/webp" -> ".webp";
            case "image/svg+xml" -> ".svg";
            default -> "";
        };
    }
}
