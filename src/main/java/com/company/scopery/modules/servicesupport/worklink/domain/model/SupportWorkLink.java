package com.company.scopery.modules.servicesupport.worklink.domain.model;
import java.time.Instant; import java.util.UUID;
public record SupportWorkLink(UUID id, UUID workspaceId, UUID supportCaseId, String targetObjectType,
        UUID targetObjectId, String linkType, int version, Instant createdAt, Instant updatedAt) {
    public static SupportWorkLink create(UUID workspaceId, UUID supportCaseId, String targetObjectType, UUID targetObjectId, String linkType) {
        Instant now = Instant.now();
        return new SupportWorkLink(UUID.randomUUID(), workspaceId, supportCaseId, targetObjectType, targetObjectId,
                linkType, 0, now, now);
    }
}
