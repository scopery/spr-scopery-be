package com.company.scopery.modules.quality.teststep.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateTestCaseStepRequest(@NotBlank String action, String expectedResult, UUID screenId, UUID componentId) {}
