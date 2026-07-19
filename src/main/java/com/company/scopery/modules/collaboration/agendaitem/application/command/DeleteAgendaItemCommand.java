package com.company.scopery.modules.collaboration.agendaitem.application.command;
import java.util.UUID;
public record DeleteAgendaItemCommand(UUID projectId, UUID meetingId, UUID agendaItemId) {}
