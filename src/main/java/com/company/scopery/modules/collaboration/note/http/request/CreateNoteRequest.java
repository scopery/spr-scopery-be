package com.company.scopery.modules.collaboration.note.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateNoteRequest(UUID agendaItemId, @NotBlank String noteType, @NotBlank String body, Boolean clientVisible) {}
