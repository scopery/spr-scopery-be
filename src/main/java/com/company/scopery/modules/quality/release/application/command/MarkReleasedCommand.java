package com.company.scopery.modules.quality.release.application.command;

import java.util.UUID;

public record MarkReleasedCommand(UUID projectId, UUID releasePackageId) {}
