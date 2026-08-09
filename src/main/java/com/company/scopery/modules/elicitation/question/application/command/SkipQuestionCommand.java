package com.company.scopery.modules.elicitation.question.application.command;

import java.util.UUID;

public record SkipQuestionCommand(
        UUID projectId,
        UUID sessionId,
        UUID questionId
) {}
