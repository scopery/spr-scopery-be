package com.company.scopery.modules.collaboration.agendaitem.application.command;
import java.util.UUID;
public record UpdateAgendaItemCommand(UUID projectId, UUID meetingId, UUID agendaItemId, String title, String description, UUID ownerUserId, String status, Integer sortOrder, Integer timebox, Boolean clientVisible) {}
