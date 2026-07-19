package com.company.scopery.modules.quality.testplan.application.command;
import java.util.UUID;
public record CreateTestPlanCommand(UUID projectId, String code, String name, String description, String testLevel, UUID qualityPlanId, UUID releasePackageId) {}
