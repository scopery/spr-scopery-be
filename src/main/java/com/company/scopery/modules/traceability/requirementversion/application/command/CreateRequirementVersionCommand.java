package com.company.scopery.modules.traceability.requirementversion.application.command;
import java.util.UUID;
public record CreateRequirementVersionCommand(UUID requirementId, UUID workspaceId, String title, String description, String changeSummary, UUID createdByUserId) {}
