package com.company.scopery.modules.collaboration.actionitem.application.command;
import java.util.UUID;
public record CompleteActionItemCommand(UUID projectId, UUID actionItemId, String note) {}
