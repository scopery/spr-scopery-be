package com.company.scopery.modules.traceability.screen.application.action;

import com.company.scopery.modules.knowledge.shared.storage.ObjectStorageProvider;
import com.company.scopery.modules.traceability.screen.application.command.RequestScreenMockupUploadCommand;
import com.company.scopery.modules.traceability.screen.application.response.ScreenMockupUploadResponse;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreenRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityStorageLocations;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Component
public class RequestScreenMockupUploadAction {

    private final RegistryScreenRepository screenRepo;
    private final ObjectStorageProvider storageProvider;
    private final TraceabilityAuthorizationService authorization;

    public RequestScreenMockupUploadAction(RegistryScreenRepository screenRepo,
                                           ObjectStorageProvider storageProvider,
                                           TraceabilityAuthorizationService authorization) {
        this.screenRepo = screenRepo;
        this.storageProvider = storageProvider;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public ScreenMockupUploadResponse execute(RequestScreenMockupUploadCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        screenRepo.findByIdAndApplicationId(c.screenId(), c.applicationId())
                .orElseThrow(() -> TraceabilityExceptions.screenNotFound(c.screenId()));

        var location = TraceabilityStorageLocations.SCREEN_MOCKUP;
        String objectKey = location.keyFor(c.screenId(), UUID.randomUUID() + extensionFor(c.contentType()));
        var upload = storageProvider.createPresignedUpload(objectKey, c.contentType(), location.maxSizeBytes());
        return new ScreenMockupUploadResponse(c.screenId(), upload.uploadUrl(), objectKey, upload.expiresAt(), location.maxSizeBytes());
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
