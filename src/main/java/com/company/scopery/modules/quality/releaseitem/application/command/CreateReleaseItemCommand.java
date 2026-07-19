package com.company.scopery.modules.quality.releaseitem.application.command;
import java.util.UUID;
public record CreateReleaseItemCommand(UUID projectId, UUID releasePackageId, String targetType, UUID targetId, Boolean required, String status, String notes) {}
