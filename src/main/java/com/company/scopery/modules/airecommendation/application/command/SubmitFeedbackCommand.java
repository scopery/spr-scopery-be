package com.company.scopery.modules.airecommendation.application.command;

import java.util.UUID;

public record SubmitFeedbackCommand(
        UUID workspaceId,
        UUID actorId,
        String suggestionRef,
        Boolean helpful,
        String reasonCode,
        String comment,
        String observedOutcome
) {}
