package com.company.scopery.modules.collaboration.actionitem.application.command;
import java.time.LocalDate; import java.util.UUID;
public record CreateActionItemCommand(UUID projectId, UUID meetingId, UUID agendaItemId, String title, String description, String ownerType, UUID ownerId, LocalDate dueDate, Boolean clientVisible) {}
