package com.company.scopery.modules.quality.testcase.application.command;

import java.util.UUID;

public record DeleteTestCaseCommand(UUID testCaseId, UUID projectId) {}
