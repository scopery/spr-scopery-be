package com.company.scopery.modules.quality.testcase.http.request;
import jakarta.validation.constraints.NotBlank; import java.util.UUID;
public record CreateTestCaseRequest(@NotBlank String title, String code, String description, @NotBlank String type, @NotBlank String priority, UUID testSuiteId, String preconditions, String expectedResult) {}
