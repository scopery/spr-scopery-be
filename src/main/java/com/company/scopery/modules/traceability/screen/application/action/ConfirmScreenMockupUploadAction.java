package com.company.scopery.modules.traceability.screen.application.action;

import com.company.scopery.modules.knowledge.shared.storage.ObjectStorageProvider;
import com.company.scopery.modules.traceability.screen.application.command.ConfirmScreenMockupUploadCommand;
import com.company.scopery.modules.traceability.screen.application.response.ScreenMockupConfirmResponse;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreen;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreenRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityStorageLocations;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ConfirmScreenMockupUploadAction {

    private final RegistryScreenRepository screenRepo;
    private final ObjectStorageProvider storageProvider;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public ConfirmScreenMockupUploadAction(RegistryScreenRepository screenRepo,
                                           ObjectStorageProvider storageProvider,
                                           TraceabilityAuthorizationService authorization,
                                           TraceabilityActivityLogger activityLogger) {
        this.screenRepo = screenRepo;
        this.storageProvider = storageProvider;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ScreenMockupConfirmResponse execute(ConfirmScreenMockupUploadCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        RegistryScreen screen = screenRepo.findByIdAndApplicationId(c.screenId(), c.applicationId())
                .orElseThrow(() -> TraceabilityExceptions.screenNotFound(c.screenId()));

        var location = TraceabilityStorageLocations.SCREEN_MOCKUP;
        if (!c.objectKey().startsWith(location.prefixFor(c.screenId()))) {
            throw TraceabilityExceptions.uploadObjectNotFound(c.objectKey());
        }

        var meta = storageProvider.head(c.objectKey());
        if (meta == null) throw TraceabilityExceptions.uploadObjectNotFound(c.objectKey());
        if (meta.sizeBytes() > location.maxSizeBytes()) {
            storageProvider.delete(c.objectKey());
            throw TraceabilityExceptions.uploadFileTooLarge(meta.sizeBytes(), location.maxSizeBytes());
        }

        if (screen.mockupObjectKey() != null && !screen.mockupObjectKey().equals(c.objectKey())) {
            storageProvider.delete(screen.mockupObjectKey());
        }

        RegistryScreen updated = screenRepo.save(screen.withMockup(c.objectKey()));
        String mockupUrl = storageProvider.createPresignedDownload(c.objectKey(), null).downloadUrl();

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN, c.screenId(),
                TraceabilityActivityActions.SCREEN_MOCKUP_UPLOADED,
                "Mockup uploaded for screen " + c.screenId());
        return new ScreenMockupConfirmResponse(updated.id(), updated.mockupObjectKey(), mockupUrl);
    }
}
