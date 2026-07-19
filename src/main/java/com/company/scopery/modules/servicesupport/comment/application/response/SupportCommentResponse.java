package com.company.scopery.modules.servicesupport.comment.application.response;
import com.company.scopery.modules.servicesupport.comment.domain.model.SupportComment;
import java.time.Instant; import java.util.UUID;
public record SupportCommentResponse(UUID id, UUID workspaceId, UUID supportCaseId, String visibility, String body,
        UUID authorUserId, boolean portalVisible, Instant createdAt) {
    public static SupportCommentResponse from(SupportComment c) {
        return new SupportCommentResponse(c.id(), c.workspaceId(), c.supportCaseId(), c.visibility(), c.body(),
                c.authorUserId(), c.isPortalVisible(), c.createdAt());
    }
}
