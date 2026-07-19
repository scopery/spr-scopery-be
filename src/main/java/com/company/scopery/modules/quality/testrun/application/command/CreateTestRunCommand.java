package com.company.scopery.modules.quality.testrun.application.command;
import java.util.UUID;
public record CreateTestRunCommand(UUID projectId, String name, String runType, UUID testPlanId, UUID testSuiteId, UUID releasePackageId) {}
