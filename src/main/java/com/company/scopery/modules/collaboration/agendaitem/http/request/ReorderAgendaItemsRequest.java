package com.company.scopery.modules.collaboration.agendaitem.http.request;
import jakarta.validation.constraints.NotEmpty;
import java.util.List; import java.util.UUID;
public record ReorderAgendaItemsRequest(@NotEmpty List<UUID> orderedIds) {}
