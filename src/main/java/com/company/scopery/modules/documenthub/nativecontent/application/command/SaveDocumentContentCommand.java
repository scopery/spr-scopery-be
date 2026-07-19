package com.company.scopery.modules.documenthub.nativecontent.application.command;

import com.company.scopery.modules.documenthub.nativecontent.domain.enums.RevisionType;

import java.util.UUID;

public record SaveDocumentContentCommand(
        UUID projectId,
        UUID documentId,
        String ast,
        long expectedBaseRevisionNo,
        Integer schemaVersion,
        RevisionType revisionType
) {}
