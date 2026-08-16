package com.company.scopery.modules.traceability.screen.application.command;
import java.util.UUID;
public record ConfirmScreenMockupUploadCommand(UUID workspaceId, UUID applicationId, UUID screenId, String objectKey) {}
