package com.company.scopery.modules.collaboration.note.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpdateNoteRequest(@NotBlank String noteType, @NotBlank String body, Boolean clientVisible) {}
