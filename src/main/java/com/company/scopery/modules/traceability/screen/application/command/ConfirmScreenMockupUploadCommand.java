package com.company.scopery.modules.traceability.screen.application.command;
import org.springframework.lang.Nullable;
import java.util.UUID;
public record ConfirmScreenMockupUploadCommand(UUID workspaceId, @Nullable UUID applicationId, UUID screenId, String objectKey) {}
