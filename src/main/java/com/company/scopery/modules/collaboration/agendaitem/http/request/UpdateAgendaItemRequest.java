package com.company.scopery.modules.collaboration.agendaitem.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record UpdateAgendaItemRequest(@NotBlank String title, String description, UUID ownerUserId, @NotBlank String status, Integer sortOrder, Integer timeboxMinutes, Boolean clientVisible) {}
