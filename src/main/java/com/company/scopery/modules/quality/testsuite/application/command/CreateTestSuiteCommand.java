package com.company.scopery.modules.quality.testsuite.application.command;
import java.util.UUID;
public record CreateTestSuiteCommand(UUID projectId, UUID testPlanId, String name, String description, UUID deliverableId, UUID scopeItemId, Integer sortOrder) {}
