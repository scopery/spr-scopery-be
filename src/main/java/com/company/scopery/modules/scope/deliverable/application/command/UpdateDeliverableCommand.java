package com.company.scopery.modules.scope.deliverable.application.command;
import java.util.UUID;
public record UpdateDeliverableCommand(UUID projectId, UUID deliverableId, String type, String title, String description) {}
