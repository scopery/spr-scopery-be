package com.company.scopery.modules.quality.qualityplan.application.command;
import java.util.UUID;
public record CreateQualityPlanCommand(UUID projectId, String code, String name, String description, String qualityObjectives, String testStrategy, String entryCriteria, String exitCriteria) {}
