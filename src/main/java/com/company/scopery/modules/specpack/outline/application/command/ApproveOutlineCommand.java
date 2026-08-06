package com.company.scopery.modules.specpack.outline.application.command;

import java.util.UUID;

public record ApproveOutlineCommand(
        UUID projectId,
        UUID sessionId,
        UUID outlineId
) {}
