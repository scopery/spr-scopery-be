package com.company.scopery.modules.externalparty.documentlink.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request payload for linking a document to an external organization or contact")
public record CreateExternalPartyDocumentLinkRequest(
        @Schema(description = "ID of the document to link to the external party", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID documentId,

        @Schema(description = "Optional note explaining the relationship between the document and the external party", example = "Signed NDA dated 2026-07-17", nullable = true)
        String linkNote) {}
