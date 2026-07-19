package com.company.scopery.modules.quality.qualityplan.application.command;
import java.util.UUID;
public record UpdateQualityPlanCommand(UUID projectId, UUID qualityPlanId, String name, String description,
        String qualityObjectives, String testStrategy, String entryCriteria, String exitCriteria) {}
