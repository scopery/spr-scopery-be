package com.company.scopery.modules.traceability.requirement.application.command;
import java.util.UUID;
public record CreateRequirementCommand(UUID projectId, UUID applicationId, String code, String title, String description, String requirementType, String priority, UUID functionalItemId, UUID nonFunctionalItemId){}
