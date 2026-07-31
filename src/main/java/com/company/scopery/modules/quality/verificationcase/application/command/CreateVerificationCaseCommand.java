package com.company.scopery.modules.quality.verificationcase.application.command;
import java.util.UUID;
public record CreateVerificationCaseCommand(
        UUID projectId, UUID requirementId, String code, String title, String description,
        String verificationMethod, String procedure, String expectedResultJson,
        String environment, String automationStatus, UUID ownerId, UUID assigneeId) {}
