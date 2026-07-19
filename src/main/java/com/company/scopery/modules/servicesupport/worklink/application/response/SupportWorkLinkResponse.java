package com.company.scopery.modules.servicesupport.worklink.application.response;
import com.company.scopery.modules.servicesupport.worklink.domain.model.SupportWorkLink;
import java.time.Instant; import java.util.UUID;
public record SupportWorkLinkResponse(UUID id, UUID workspaceId, UUID supportCaseId, String targetObjectType,
        UUID targetObjectId, String linkType, Instant createdAt) {
    public static SupportWorkLinkResponse from(SupportWorkLink d) {
        return new SupportWorkLinkResponse(d.id(), d.workspaceId(), d.supportCaseId(), d.targetObjectType(),
                d.targetObjectId(), d.linkType(), d.createdAt());
    }
}
