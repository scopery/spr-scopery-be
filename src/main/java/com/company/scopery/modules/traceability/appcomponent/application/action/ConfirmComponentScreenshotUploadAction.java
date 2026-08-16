package com.company.scopery.modules.traceability.appcomponent.application.action;

import com.company.scopery.modules.knowledge.shared.storage.ObjectStorageProvider;
import com.company.scopery.modules.traceability.appcomponent.application.command.ConfirmComponentScreenshotUploadCommand;
import com.company.scopery.modules.traceability.appcomponent.application.response.ComponentScreenshotConfirmResponse;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponent;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponentRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityStorageLocations;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ConfirmComponentScreenshotUploadAction {

    private final RegistryAppComponentRepository componentRepo;
    private final ObjectStorageProvider storageProvider;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public ConfirmComponentScreenshotUploadAction(RegistryAppComponentRepository componentRepo,
                                                   ObjectStorageProvider storageProvider,
                                                   TraceabilityAuthorizationService authorization,
                                                   TraceabilityActivityLogger activityLogger) {
        this.componentRepo = componentRepo;
        this.storageProvider = storageProvider;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ComponentScreenshotConfirmResponse execute(ConfirmComponentScreenshotUploadCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        RegistryAppComponent component = componentRepo.findById(c.componentId())
                .orElseThrow(() -> TraceabilityExceptions.appComponentNotFound(c.componentId()));

        var location = TraceabilityStorageLocations.COMPONENT_SCREENSHOT;
        if (!c.objectKey().startsWith(location.prefixFor(c.componentId()))) {
            throw TraceabilityExceptions.uploadObjectNotFound(c.objectKey());
        }

        var meta = storageProvider.head(c.objectKey());
        if (meta == null) throw TraceabilityExceptions.uploadObjectNotFound(c.objectKey());
        if (meta.sizeBytes() > location.maxSizeBytes()) {
            storageProvider.delete(c.objectKey());
            throw TraceabilityExceptions.uploadFileTooLarge(meta.sizeBytes(), location.maxSizeBytes());
        }

        if (component.screenshotObjectKey() != null && !component.screenshotObjectKey().equals(c.objectKey())) {
            storageProvider.delete(component.screenshotObjectKey());
        }

        RegistryAppComponent updated = componentRepo.save(component.withScreenshot(c.objectKey()));
        String screenshotUrl = storageProvider.createPresignedDownload(c.objectKey(), null).downloadUrl();

        activityLogger.logSuccess(TraceabilityEntityTypes.APP_COMPONENT, c.componentId(),
                TraceabilityActivityActions.COMPONENT_SCREENSHOT_UPLOADED,
                "Screenshot uploaded for component " + c.componentId());
        return new ComponentScreenshotConfirmResponse(updated.id(), updated.screenshotObjectKey(), screenshotUrl);
    }
}
