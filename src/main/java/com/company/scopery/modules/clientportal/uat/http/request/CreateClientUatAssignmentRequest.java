package com.company.scopery.modules.clientportal.uat.http.request;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateClientUatAssignmentRequest(UUID testCaseId, UUID testRunId, @NotNull UUID portalAccountId, String notes) {}
