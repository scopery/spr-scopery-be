package com.company.scopery.modules.traceability.requirementcriteria.application.command;
import java.util.UUID;
public record CreateRequirementCriteriaCommand(UUID requirementId, UUID workspaceId, String description, String acceptanceType, int displayOrder) {}
