package com.company.scopery.modules.specpack.clarification.application.command;

import java.util.UUID;

public record CreateClarificationCommand(
        UUID projectId,
        UUID sessionId,
        String code,
        String question,
        String priority,
        String source
) {}
