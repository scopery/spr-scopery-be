package com.company.scopery.modules.elicitation.session.application.response;

import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSession;

import java.time.Instant;
import java.util.UUID;

public record ElicitationSessionResponse(
        UUID id,
        UUID projectId,
        UUID scopePackageId,
        String title,
        String status,
        String createdBy,
        Instant createdAt,
        Instant updatedAt
) {
    public static ElicitationSessionResponse from(ElicitationSession session) {
        return new ElicitationSessionResponse(
                session.id(),
                session.projectId(),
                session.scopePackageId(),
                session.title(),
                session.status().name(),
                session.createdBy(),
                session.createdAt(),
                session.updatedAt()
        );
    }
}
