package com.company.scopery.modules.raid.decisionlink.application.response;

import com.company.scopery.modules.raid.decisionlink.domain.model.DecisionLink;

import java.time.Instant;
import java.util.UUID;

public record DecisionLinkResponse(
        UUID id,
        UUID decisionId,
        UUID projectId,
        String linkType,
        String targetType,
        UUID targetId,
        Instant createdAt
) {
    public static DecisionLinkResponse from(DecisionLink link) {
        return new DecisionLinkResponse(link.id(), link.decisionId(), link.projectId(), link.linkType(),
                link.targetType(), link.targetId(), link.createdAt());
    }
}
