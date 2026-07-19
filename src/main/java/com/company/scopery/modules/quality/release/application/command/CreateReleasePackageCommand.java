package com.company.scopery.modules.quality.release.application.command;
import java.time.LocalDate; import java.util.UUID;
public record CreateReleasePackageCommand(UUID projectId, String code, String versionLabel, String name, String description, String releaseType, LocalDate plannedReleaseDate) {}
