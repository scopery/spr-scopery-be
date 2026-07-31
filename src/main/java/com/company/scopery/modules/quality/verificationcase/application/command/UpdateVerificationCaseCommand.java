package com.company.scopery.modules.quality.verificationcase.application.command;
import java.util.UUID;
public record UpdateVerificationCaseCommand(
        UUID projectId, UUID verificationCaseId, String title, String description,
        String verificationMethod, String procedure, String expectedResultJson,
        String environment, String lifecycleStatus, String automationStatus,
        UUID ownerId, UUID assigneeId, Long version) {}
