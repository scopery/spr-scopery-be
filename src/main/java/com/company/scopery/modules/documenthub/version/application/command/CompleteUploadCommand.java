package com.company.scopery.modules.documenthub.version.application.command;

import java.util.UUID;

public record CompleteUploadCommand(
        UUID projectId,
        UUID documentId,
        UUID versionId,
        String checksum,
        Long fileSizeBytes
) {}
