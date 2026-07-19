package com.company.scopery.modules.quality.release.application.command;

import java.util.UUID;

public record CheckReleaseReadinessCommand(UUID projectId, UUID releasePackageId) {}
