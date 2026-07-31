package com.company.scopery.modules.quality.testcase.http.request;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
public record UpdateTestCaseRequest(String title, String description,
        @Schema(allowableValues={"FUNCTIONAL","INTEGRATION","REGRESSION","PERFORMANCE","SECURITY","USABILITY","EXPLORATORY"}) String type,
        @Schema(allowableValues={"LOW","MEDIUM","HIGH","CRITICAL"}) String priority,
        @Schema(allowableValues={"DRAFT","APPROVED","ARCHIVED"}) String status,
        UUID useCaseId, UUID assigneeId,
        @Schema(allowableValues={"MANUAL","PLANNED","AUTOMATED"}) String automationStatus,
        String preconditions, String expectedResult, Long version) {}
