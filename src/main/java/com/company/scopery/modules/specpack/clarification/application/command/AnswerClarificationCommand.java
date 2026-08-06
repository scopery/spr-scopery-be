package com.company.scopery.modules.specpack.clarification.application.command;

import java.util.UUID;

public record AnswerClarificationCommand(
        UUID projectId,
        UUID sessionId,
        UUID clarificationId,
        String answer
) {}
