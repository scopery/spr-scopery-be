package com.company.scopery.modules.specpack.outline.application.command;

import java.util.Map;
import java.util.UUID;

public record CreateOutlineCommand(
        UUID projectId,
        UUID sessionId,
        Map<String, Object> outlineJson
) {}
