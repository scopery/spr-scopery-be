package com.company.scopery.modules.quality.deployment.http.request;
import jakarta.validation.constraints.NotBlank;
public record RollbackDeploymentRequest(@NotBlank String rollbackReason) {}
