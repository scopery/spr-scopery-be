package com.company.scopery.modules.collaboration.actionitem.application.command;
import java.util.UUID;
public record ArchiveActionItemCommand(UUID projectId, UUID actionItemId) {}
