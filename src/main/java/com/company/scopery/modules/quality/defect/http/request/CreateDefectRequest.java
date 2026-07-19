package com.company.scopery.modules.quality.defect.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateDefectRequest(@NotBlank String title, String code, String description, @NotBlank String category,
        @NotBlank String severity, @NotBlank String priority, String reproductionSteps, String expectedResult,
        String actualResult, UUID sourceTestCaseResultId) {}
