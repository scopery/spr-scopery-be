package com.company.scopery.modules.quality.rollbackplan.application.command;
import java.util.UUID;
public record CreateRollbackPlanCommand(UUID projectId, UUID releasePackageId, UUID deploymentRecordId, String title, String description, UUID ownerUserId, String stepsJson) {}
