package com.company.scopery.modules.traceability.requirement.application.command;

import java.util.UUID;

public record DeleteRequirementCommand(UUID projectId, UUID requirementId) {}
