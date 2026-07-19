package com.company.scopery.modules.projectbaseline.baseline.application.command;
import java.util.UUID;
public record UpdateBaselineCommand(UUID projectId, UUID baselineId, String name, String description) {}
