package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record AddUseCaseAcceptanceCriterionCommand(
        UUID projectId, UUID useCaseId,
        String title, String givenText, String whenText, String thenText, int displayOrder
) {}
