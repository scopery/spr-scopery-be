package com.company.scopery.modules.quality.qualityplan.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpdateQualityPlanRequest(@NotBlank String name, String description, String qualityObjectives,
        String testStrategy, String entryCriteria, String exitCriteria) {}
