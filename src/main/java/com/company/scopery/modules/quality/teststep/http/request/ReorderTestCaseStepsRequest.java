package com.company.scopery.modules.quality.teststep.http.request;
import jakarta.validation.constraints.NotEmpty;
import java.util.List; import java.util.UUID;
public record ReorderTestCaseStepsRequest(@NotEmpty List<UUID> orderedStepIds) {}
