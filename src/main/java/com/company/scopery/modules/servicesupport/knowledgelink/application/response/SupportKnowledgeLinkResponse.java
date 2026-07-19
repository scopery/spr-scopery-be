package com.company.scopery.modules.servicesupport.knowledgelink.application.response;
import com.company.scopery.modules.servicesupport.knowledgelink.domain.model.SupportKnowledgeLink;
import java.time.Instant; import java.util.UUID;
public record SupportKnowledgeLinkResponse(UUID id, UUID workspaceId, UUID supportCaseId, UUID documentId,
        String linkType, boolean clientVisible, Instant createdAt) {
    public static SupportKnowledgeLinkResponse from(SupportKnowledgeLink d) {
        return new SupportKnowledgeLinkResponse(d.id(), d.workspaceId(), d.supportCaseId(), d.documentId(),
                d.linkType(), d.clientVisible(), d.createdAt());
    }
}
