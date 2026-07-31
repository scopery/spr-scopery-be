package com.company.scopery.modules.quality.testcase.http.request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank; import java.util.UUID;
public record CreateTestCaseRequest(@NotBlank String title, String code, String description,
        @Schema(allowableValues={"FUNCTIONAL","INTEGRATION","REGRESSION","PERFORMANCE","SECURITY","USABILITY","EXPLORATORY"}) String type,
        @Schema(allowableValues={"LOW","MEDIUM","HIGH","CRITICAL"}) String priority,
        UUID testSuiteId, UUID useCaseId, String preconditions, String expectedResult, UUID assigneeId,
        @Schema(allowableValues={"MANUAL","PLANNED","AUTOMATED"}) String automationStatus) {}
