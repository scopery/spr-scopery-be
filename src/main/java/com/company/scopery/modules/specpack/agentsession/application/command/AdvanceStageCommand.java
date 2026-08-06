package com.company.scopery.modules.specpack.agentsession.application.command;

import java.util.Map;
import java.util.UUID;

public record AdvanceStageCommand(
        UUID projectId,
        UUID sessionId,
        String stageCode,
        Map<String, Object> result
) {}
