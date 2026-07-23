package com.company.scopery.modules.traceability.requirement.application.command;
import java.util.UUID;
public record UpdateRequirementCommand(UUID id, UUID projectId, String title, String description, String priority, String requirementType, UUID applicationId, UUID functionalItemId, UUID nonFunctionalItemId){}
