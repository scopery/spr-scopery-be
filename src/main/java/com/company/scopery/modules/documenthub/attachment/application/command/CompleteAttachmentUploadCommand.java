package com.company.scopery.modules.documenthub.attachment.application.command;

import java.util.UUID;

public record CompleteAttachmentUploadCommand(
        UUID projectId,
        UUID documentId,
        UUID attachmentId
) {}
