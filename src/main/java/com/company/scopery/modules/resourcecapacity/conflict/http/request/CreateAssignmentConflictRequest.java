package com.company.scopery.modules.resourcecapacity.conflict.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateAssignmentConflictRequest(UUID resourceProfileId, @NotBlank String conflictType, @NotBlank String severity, String description) {}
