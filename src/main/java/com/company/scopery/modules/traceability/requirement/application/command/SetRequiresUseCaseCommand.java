package com.company.scopery.modules.traceability.requirement.application.command;

import java.util.UUID;

public record SetRequiresUseCaseCommand(UUID requirementId, UUID projectId, String value) {}
