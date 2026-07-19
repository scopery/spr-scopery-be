package com.company.scopery.modules.quality.rollbackplan.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateRollbackPlanRequest(UUID releasePackageId, UUID deploymentRecordId, @NotBlank String title, @NotBlank String description, UUID ownerUserId, String stepsJson) {}
