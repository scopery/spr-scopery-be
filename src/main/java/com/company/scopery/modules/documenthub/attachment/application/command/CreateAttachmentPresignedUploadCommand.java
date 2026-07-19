package com.company.scopery.modules.documenthub.attachment.application.command;

import java.util.UUID;

public record CreateAttachmentPresignedUploadCommand(
        UUID projectId,
        UUID documentId,
        String blockId,
        String fileName,
        String mediaType,
        Long fileSizeBytes
) {}
