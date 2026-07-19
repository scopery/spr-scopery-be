package com.company.scopery.modules.traceability.requirement.application.command;
import java.util.UUID;
public record RejectRequirementCommand(UUID id, UUID projectId){}
