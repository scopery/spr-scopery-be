package com.company.scopery.modules.quality.deploymentenv.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateDeploymentEnvironmentRequest(@NotBlank String code, @NotBlank String name, @NotBlank String environmentType, String description) {}
