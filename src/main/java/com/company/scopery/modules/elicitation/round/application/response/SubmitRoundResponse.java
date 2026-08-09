package com.company.scopery.modules.elicitation.round.application.response;

import java.util.List;
import java.util.UUID;

public record SubmitRoundResponse(
        ElicitationRoundResponse round,
        List<RoundEvaluation> evaluations,
        boolean shouldContinue,
        String evaluationSummary
) {
    public record RoundEvaluation(
            UUID questionId,
            String clarityLevel,
            String feedback,
            String conflictNote
    ) {}
}
