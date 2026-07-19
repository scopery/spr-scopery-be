package com.company.scopery.modules.scope.deliverable.application.command;
import java.util.UUID;
public record ArchiveDeliverableCommand(UUID projectId, UUID deliverableId) {}
