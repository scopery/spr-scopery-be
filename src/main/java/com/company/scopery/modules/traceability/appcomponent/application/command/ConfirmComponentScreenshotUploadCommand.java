package com.company.scopery.modules.traceability.appcomponent.application.command;
import java.util.UUID;
public record ConfirmComponentScreenshotUploadCommand(UUID workspaceId, UUID applicationId, UUID componentId, String objectKey) {}
