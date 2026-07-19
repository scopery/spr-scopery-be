package com.company.scopery.modules.servicesupport.comment.domain.model;
import java.time.Instant; import java.util.UUID;
public record SupportComment(UUID id, UUID workspaceId, UUID supportCaseId, String visibility, String body,
        UUID authorUserId, Instant createdAt) {
    public static SupportComment internal(UUID workspaceId, UUID caseId, String body, UUID authorId) {
        return new SupportComment(UUID.randomUUID(), workspaceId, caseId, "INTERNAL", body, authorId, Instant.now());
    }
    public static SupportComment clientVisible(UUID workspaceId, UUID caseId, String body, UUID authorId) {
        return new SupportComment(UUID.randomUUID(), workspaceId, caseId, "CLIENT_VISIBLE", body, authorId, Instant.now());
    }
    public boolean isPortalVisible() { return "CLIENT_VISIBLE".equals(visibility); }
}
