package com.company.scopery.modules.clientportal.uat.application.command;
import java.util.UUID;
public record CreateClientUatAssignmentCommand(UUID projectId, UUID testCaseId, UUID testRunId, UUID portalAccountId, String notes) {}
