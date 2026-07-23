package com.company.scopery.modules.traceability.nfrscope.application.command;

import com.company.scopery.modules.traceability.nfrscope.domain.enums.NfrTargetType;

import java.util.UUID;

public record LinkNfrScopeTargetCommand(UUID projectId, UUID nfrId, UUID targetId, NfrTargetType targetType) {
}
