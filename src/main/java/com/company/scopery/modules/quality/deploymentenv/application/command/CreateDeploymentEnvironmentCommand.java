package com.company.scopery.modules.quality.deploymentenv.application.command;
import java.util.UUID;
public record CreateDeploymentEnvironmentCommand(UUID projectId, String code, String name, String environmentType, String description) {}
