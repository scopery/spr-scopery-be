package com.company.scopery.modules.quality.testrun.application.command;
import java.util.UUID;
public record UpdateTestCaseResultCommand(UUID projectId, UUID testRunId, UUID resultId, String result, String comment, Long version) {}
