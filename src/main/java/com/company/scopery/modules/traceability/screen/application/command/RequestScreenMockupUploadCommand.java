package com.company.scopery.modules.traceability.screen.application.command;
import java.util.UUID;
public record RequestScreenMockupUploadCommand(UUID workspaceId, UUID applicationId, UUID screenId, String contentType) {}
