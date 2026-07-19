package com.company.scopery.modules.collaboration.agendaitem.application.command;
import java.util.List; import java.util.UUID;
public record ReorderAgendaItemsCommand(UUID projectId, UUID meetingId, List<UUID> orderedIds) {}
