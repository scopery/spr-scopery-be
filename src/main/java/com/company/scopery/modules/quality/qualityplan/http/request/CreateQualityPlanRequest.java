package com.company.scopery.modules.quality.qualityplan.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateQualityPlanRequest(@NotBlank String name, String code, String description, String qualityObjectives, String testStrategy, String entryCriteria, String exitCriteria) {}
