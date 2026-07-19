package com.company.scopery.modules.collaboration.note.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateChangeRequestFromNoteRequest(
        @NotBlank String code, String title, String description, String changeType, String priority,
        UUID baselineId, String reason) {}
