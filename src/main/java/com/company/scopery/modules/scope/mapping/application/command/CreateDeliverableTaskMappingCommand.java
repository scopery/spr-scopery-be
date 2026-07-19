package com.company.scopery.modules.scope.mapping.application.command;
import java.util.UUID;
public record CreateDeliverableTaskMappingCommand(UUID projectId, UUID deliverableId, UUID taskId, String mappingType) {}
