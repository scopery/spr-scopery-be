package com.company.scopery.modules.collaboration.agendaitem.application.command;
import java.util.UUID;
public record CreateAgendaItemCommand(UUID projectId, UUID meetingId, String title, String description, UUID ownerUserId, Integer sortOrder, Integer timebox, Boolean clientVisible) {}
