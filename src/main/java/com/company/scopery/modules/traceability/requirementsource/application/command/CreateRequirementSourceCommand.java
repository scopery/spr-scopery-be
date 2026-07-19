package com.company.scopery.modules.traceability.requirementsource.application.command;
import java.util.UUID;
public record CreateRequirementSourceCommand(UUID requirementId, UUID workspaceId, String sourceType, String sourceReference, String description) {}
