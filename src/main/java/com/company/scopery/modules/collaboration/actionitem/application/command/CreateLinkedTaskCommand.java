package com.company.scopery.modules.collaboration.actionitem.application.command;
import java.util.UUID;
public record CreateLinkedTaskCommand(UUID projectId, UUID actionItemId, UUID taskId) {}
