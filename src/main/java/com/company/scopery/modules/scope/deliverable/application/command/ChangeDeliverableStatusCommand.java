package com.company.scopery.modules.scope.deliverable.application.command;
import java.util.UUID;
public record ChangeDeliverableStatusCommand(UUID projectId, UUID deliverableId, String status) {}
