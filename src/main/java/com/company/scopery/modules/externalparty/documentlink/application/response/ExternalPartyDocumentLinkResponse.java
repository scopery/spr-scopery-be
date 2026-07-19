package com.company.scopery.modules.externalparty.documentlink.application.response;

import com.company.scopery.modules.externalparty.documentlink.domain.model.ExternalPartyDocumentLink;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representation of a document link associated with an external organization or contact")
public record ExternalPartyDocumentLinkResponse(
        @Schema(description = "Unique identifier of the document link record", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the workspace this link belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID workspaceId,

        @Schema(description = "ID of the external organization this link is associated with", example = "550e8400-e29b-41d4-a716-446655440002", nullable = true)
        UUID externalOrganizationId,

        @Schema(description = "ID of the external contact this link is associated with", example = "550e8400-e29b-41d4-a716-446655440003", nullable = true)
        UUID externalContactId,

        @Schema(description = "ID of the linked document", example = "550e8400-e29b-41d4-a716-446655440004")
        UUID documentId,

        @Schema(description = "Note explaining the relationship between the document and the external party", example = "Signed NDA dated 2026-07-17", nullable = true)
        String linkNote,

        @Schema(description = "Timestamp when the link was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the link was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt) {

    public static ExternalPartyDocumentLinkResponse from(ExternalPartyDocumentLink l) {
        return new ExternalPartyDocumentLinkResponse(l.id(), l.workspaceId(),
                l.externalOrganizationId(), l.externalContactId(),
                l.documentId(), l.linkNote(), l.createdAt(), l.updatedAt());
    }
}
