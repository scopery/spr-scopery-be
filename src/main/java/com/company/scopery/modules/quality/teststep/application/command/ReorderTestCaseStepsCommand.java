package com.company.scopery.modules.quality.teststep.application.command;
import java.util.List; import java.util.UUID;
public record ReorderTestCaseStepsCommand(UUID projectId, UUID testCaseId, List<UUID> orderedStepIds) {}
