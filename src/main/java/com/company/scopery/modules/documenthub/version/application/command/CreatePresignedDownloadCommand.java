package com.company.scopery.modules.documenthub.version.application.command;

import java.util.UUID;

public record CreatePresignedDownloadCommand(
        UUID projectId,
        UUID versionId
) {}
