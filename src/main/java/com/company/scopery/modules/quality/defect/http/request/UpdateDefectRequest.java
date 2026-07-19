package com.company.scopery.modules.quality.defect.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpdateDefectRequest(@NotBlank String title, String description, String category, String severity,
        String priority, String reproductionSteps, String expectedResult, String actualResult, String environmentNotes) {}
