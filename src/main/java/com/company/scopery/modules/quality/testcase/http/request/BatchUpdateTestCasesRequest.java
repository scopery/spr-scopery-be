package com.company.scopery.modules.quality.testcase.http.request;
import jakarta.validation.constraints.NotEmpty;
import java.util.List; import java.util.UUID;
public record BatchUpdateTestCasesRequest(@NotEmpty List<UUID> testCaseIds, BatchChanges changes) {
    public record BatchChanges(String priority, String status, UUID assigneeId, String automationStatus) {}
}
