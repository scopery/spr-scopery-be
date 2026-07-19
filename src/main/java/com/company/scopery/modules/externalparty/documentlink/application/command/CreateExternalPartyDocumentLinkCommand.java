package com.company.scopery.modules.externalparty.documentlink.application.command;

import java.util.UUID;

public record CreateExternalPartyDocumentLinkCommand(
        UUID workspaceId,
        UUID externalOrganizationId,
        UUID externalContactId,
        UUID documentId,
        String linkNote) {}
