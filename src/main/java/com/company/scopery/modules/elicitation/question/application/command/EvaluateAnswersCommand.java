package com.company.scopery.modules.elicitation.question.application.command;

import java.util.UUID;

public record EvaluateAnswersCommand(
        UUID projectId,
        UUID sessionId
) {}
