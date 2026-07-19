package com.company.scopery.modules.quality.testcase.application.command;
import java.util.UUID;
public record CreateTestCaseCommand(UUID projectId, UUID testSuiteId, String code, String title, String description, String type, String priority, String preconditions, String expectedResult) {}
