package com.company.scopery.modules.collaboration.agendaitem.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateAgendaItemRequest(@NotBlank String title, String description, UUID ownerUserId, Integer sortOrder, Integer timeboxMinutes, Boolean clientVisible) {}
