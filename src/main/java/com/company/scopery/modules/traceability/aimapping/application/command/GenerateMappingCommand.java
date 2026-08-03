package com.company.scopery.modules.traceability.aimapping.application.command;

import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingRelationType;
import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingScope;

import java.util.UUID;

public record GenerateMappingCommand(
        UUID projectId,
        MappingRelationType relationType,
        MappingScope scope,
        UUID modelDeploymentId,
        UUID requestedBy
) {}
