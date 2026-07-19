package com.company.scopery.modules.collaboration.actionitem.application.command;
import java.time.LocalDate; import java.util.UUID;
public record UpdateActionItemCommand(UUID projectId, UUID actionItemId, String title, String description, String ownerType, UUID ownerId, LocalDate dueDate, String status, Boolean clientVisible) {}
