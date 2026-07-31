package com.company.scopery.modules.quality.testcase.http.request;
import jakarta.validation.constraints.NotEmpty;
import java.util.List; import java.util.UUID;
public record BatchCreateTestCasesRequest(@NotEmpty List<CreateTestCaseItemRequest> items) {
    public record CreateTestCaseItemRequest(String title, String code, String description, String type, String priority,
            UUID testSuiteId, UUID useCaseId, String preconditions, String expectedResult, UUID assigneeId, String automationStatus) {}
}
