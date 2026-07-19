package com.company.scopery.modules.raid.raidlink.application.response;

import com.company.scopery.modules.raid.raidlink.domain.model.RaidLink;

import java.time.Instant;
import java.util.UUID;

public record RaidLinkResponse(
        UUID id,
        UUID raidItemId,
        UUID projectId,
        String linkType,
        String targetType,
        UUID targetId,
        Instant createdAt
) {
    public static RaidLinkResponse from(RaidLink link) {
        return new RaidLinkResponse(link.id(), link.raidItemId(), link.projectId(), link.linkType(),
                link.targetType(), link.targetId(), link.createdAt());
    }
}
