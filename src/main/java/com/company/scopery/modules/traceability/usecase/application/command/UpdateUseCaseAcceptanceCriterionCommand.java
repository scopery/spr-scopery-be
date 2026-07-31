package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record UpdateUseCaseAcceptanceCriterionCommand(
        UUID projectId, UUID useCaseId, UUID criterionId,
        String title, String givenText, String whenText, String thenText, int displayOrder
) {}
