package com.company.scopery.modules.elicitation.question.application.command;

import java.util.UUID;

public record GenerateQuestionsCommand(
        UUID projectId,
        UUID sessionId
) {}
